package de.viadee.javakurs.services;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import io.github.classgraph.ClassGraph;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Infos zum Firestore: https://github.com/googleapis/java-firestore
 */
public class FirestoreService {

    protected Firestore firestore;

    public FirestoreService() {
    }

    public boolean logIntoFirebase() {
        try {
            String credentials = new ClassGraph().acceptPathsNonRecursive("/").scan()
                    .getAllResources().getPaths()
                    .stream()
                    .filter((name) -> name.toLowerCase().endsWith(".json")).findFirst().orElse("");
            String projectid = credentials.substring(0, credentials.indexOf("-firebase-adminsdk"));
            InputStream serviceAccount = getClass().getResourceAsStream("/" + credentials);
            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://" + projectid + ".firebaseio.com")
                    .setProjectId(projectid)
                    .build();
            FirebaseApp.initializeApp(firebaseOptions);
            firestore = FirestoreClient.getFirestore();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    public synchronized boolean updateGamestate(int playerNumber, String gameState) {
        if (firestore != null && playerNumber > 0) {
            try {
                Map<String, String> data = new TreeMap<>();
                data.put("data", gameState);
                ApiFuture<WriteResult> future = firestore.collection("gamestate").document("state"+playerNumber).set(data);
                WriteResult result = future.get(5000, TimeUnit.MILLISECONDS);
                return true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public Observable<String> getGamestates(int playerNumber) {
        BehaviorSubject<String> result = BehaviorSubject.create();
        firestore.collection("gamestate").document("state"+playerNumber).addSnapshotListener(
                (d, e) -> {
                    result.onNext((String) d.get("data"));
                }
        );
        return result.filter((s) -> s.length() > 0);
    }

}
