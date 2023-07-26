package com.dionis.escolinhajdb.data.repository

import android.net.Uri
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.util.FireStoreCollection
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    var storageReference: StorageReference,
//    val appPreferences: SharedPreferences,
//    val gson: Gson

) : AuthRepository {

    override fun getCoach(result: (UiState<List<Coach>>) -> Unit) {
        database.collection(FireStoreCollection.COACH)
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener {
                val coaches = arrayListOf<Coach>()
                for (document in it) {
                    val coach = document.toObject(Coach::class.java)
                    coaches.add(coach)
                }
                result.invoke(
                    UiState.Success(coaches)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }


    override fun updateUserInfo(coach: Coach, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.COACH).document(coach.id)
        document
            .set(coach)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun recoveryCoach(id: String, result: (UiState<Coach>) -> Unit) {
        database.collection(FireStoreCollection.COACH).document(id)
            .get()
            .addOnCompleteListener {
                val coach = it.result.toObject(Coach::class.java)

                result.invoke(
                    UiState.Success(coach!!)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }


    override fun storeSession(id: String, result: (Coach?) -> Unit) {
        database.collection(FireStoreCollection.COACH).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObject(Coach::class.java)
                    result.invoke(user)
                } else {
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }


    override fun registerUser(
        email: String,
        password: String,
        coach: Coach,
        result: (UiState<String>) -> Unit,
    ) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    coach.id = it.result.user?.uid ?: ""
                    updateUserInfo(coach) { state ->
                        when (state) {
                            is UiState.Success -> {
                                if (it == null) {
                                    result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                } else {
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                // E-mail de verificação enviado com sucesso
                                                result.invoke(UiState.Success("User register successfully! Please check email for verification."))
                                            } else {
                                                // Falha ao enviar o e-mail de verificação
                                                result.invoke(UiState.Failure("Failed to send verification email."))
                                            }
                                        }
//                                    result.invoke(
//                                        UiState.Success("User register successfully!")
//                                    )
                                }
                            }
                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }
                            else -> {}
                        }
                    }
                } else {
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }

    }

    override fun login(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null && currentUser.isEmailVerified) {
                        storeSession(id = it.result.user?.uid ?: "") { coach ->
                            if (coach == null) {
                                result.invoke(UiState.Failure("Failed to store local session"))
                            } else {
                                result.invoke(UiState.Success(coach.id))
                            }
                        }
                    } else {
                        result.invoke(UiState.Failure("Verificação de e-mail necessária. Verifique seu e-mail para confirmar."))
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.message))
            }
    }

    override suspend fun uploadImage(fileUri: Uri, onResult: (UiState<Uri>) -> Unit) {
        try {
            val uri: Uri = withContext(Dispatchers.IO) {
                storageReference.child(FireStoreCollection.COACH).child(fileUri.lastPathSegment ?: "${System.currentTimeMillis()}")
                    .putFile(fileUri)
                    .await()
                    .storage
                    .downloadUrl
                    .await()
            }
            onResult.invoke(UiState.Success(uri))
        } catch (e: FirebaseException) {
            onResult.invoke(UiState.Failure(e.message))
        } catch (e: Exception) {
            onResult.invoke(UiState.Failure(e.message))
        }
    }


    override suspend fun authenticateUser(user: FirebaseUser?, currentPassword: String, onResult: (UiState<String>) -> Unit) {
        val credentials = EmailAuthProvider.getCredential(user?.email.toString(), currentPassword)
        user?.reauthenticate(credentials)
            ?.addOnSuccessListener {
                onResult.invoke(UiState.Success("sucesso"))
            }
            ?.addOnFailureListener {
                onResult.invoke((UiState.Failure("Senha atual incorreta")))
            }
    }

    override suspend fun sendPasswordResetEmail(email: String, onResult: (UiState<String>) -> Unit) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResult.invoke(UiState.Success("Link de verificação enviado ao email: $email"))
            }
            .addOnFailureListener {
                val errorMessage = it.message ?: "Erro desconhecido"
                onResult.invoke(UiState.Failure(errorMessage))
            }
    }

    override suspend fun changePassword(user: FirebaseUser?, newPassword: String, onResult: (UiState<String>) -> Unit) {

        user?.updatePassword(newPassword)
            ?.addOnSuccessListener {
                onResult.invoke(UiState.Success("Senha alterada com sucesso"))
            }
            ?.addOnFailureListener {
                onResult.invoke((UiState.Failure(it.message)))
            }


//        try {
//            user?.updatePassword(newPassword)
//            onResult.invoke(UiState.Success("Senha alterada com sucesso!"))
//        } catch (e: FirebaseException) {
//            onResult.invoke(UiState.Failure(e.message))
//        }


    }


}






