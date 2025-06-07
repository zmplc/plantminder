# Plantminder - Documentazione

Questa è una breve guida ai file principali che compongono l'app android Plantminder. 
L'obiettivo è fornire una panoramica generale della struttura del progetto e della funzionalità dell'applicazione.

---

## File Sorgente Principali (`app\src\main\java\com\zmplc\plantminder`)

### Activities

Le Activities sono:

-   **`SplashScreenActivity.kt`**:
    * *Descrizione:* Schermata che appare per 2 secondi che mostra il logo della app con uno sfondo che anticipa la schermata per effettuare login o registrazione
-   **`WelcomeActivity.kt`**:
    * *Descrizione:* Schermata per login o registrazione utilizzando Firebase Auth e Firebase UI. La registrazione e il login possono essere effettuati tramite email e password oppure con _Accedi con Google_, inoltre è richiesto di effettuare la verifica dell'email dopo la registrazione prima di poter accedere con il proprio account.
-   **`MainActivity.kt`**:
    * *Descrizione:* Schermata per i vari fragment con una toolbar (per impostazioni e logout) e una bottom navigation per passare tra le varie schermate.

### Fragments

I Fragments sono:

-   **`HomeFragment.kt`**:
    * *Descrizione:* Fragment principale dell'applicazione che mostra le piante del giardino dell'utente e un bottone per passare al fragment `PlantListFragment.kt` per poter scegliere le piante da aggiungere al proprio giardino.
-   **`PlantListFragment.kt`**:
    * *Descrizione:* Fragment che mostra le piante disponibili che possono essere aggiunte al giardino. Le piante sono mostrate con delle card.
-   **`PlantDetailFragment.kt`**:
	- *Descrizione:* Fragment per i dettagli della pianta scelta dalla lista. Viene mostrato il nome, il nome scientifico, la descrizione, la temperatura, la luce e l'irrigazione richiesta per la pianta.
-   **`UserPlantFragment.kt`**: 
	- *Descrizione:* Fragment per i dettagli della pianta selezionata dall'utente dal giardino. In questa schermata oltre a vedere i dettagli si può rinominare ed eliminare la pianta.
-   **`ProfileFragment.kt`**:
		- *Descrizione:* Fragment per mostrare il profilo dell'utente (nome, email, piante salvate), modificare la password ed effettuare il logout.
-   **`SettingsFragment.kt`**:
	- *Descrizione:* Fragment dove l'utente può modificare il tema dell'app (chiaro/scuro), attivare/disattivare le notifiche, cancellare tutte le piante dal suo giardino e un bottone che lo porta alla documentazione.

### Adapters

Gli Adapters collegano i dati (ad esempio l'elenco delle piante) a viste dell'interfaccia utente come `RecyclerView`.

-   **`PlantAdapter.kt`**:
    * *Descrizione:* Gestisce la visualizzazione dell'elenco delle piante, mostrate in `PlantListFragment`, salvate nel database che l'utente può aggiungere al suo giardino.
    * 
### Models (Data Classes)

Classi che rappresentano la struttura dei dati utilizzati nell'app.

-   **`Plant.kt`**:
    * *Descrizione:* Classe dati per le piante (id, nome, nomeScientifico, descrizione, tipo, temperatura, luce, acqua).
-   **`GardenPlant.kt`**:
    * *Descrizione:* Classe dati per le piante salvate dall'utente  (id, nomePersonalizzato, nome).

### ViewModels

I ViewModels gestiscono i dati relativi all'interfaccia utente (ciò che viene mostrato nella PlantList e nel Garden).

-   **`PlantListViewModel.kt`**:
    * *Descrizione:* Per lista piante in `PlantListFragment`.
-   **`GardenViewModel.kt`**:
    * *Descrizione:* Per piante del giardino.

---

## File di Risorse (`app/src/main/res/`)

### Layout (`app/src/main/res/layout/`)

File XML che definiscono l'interfaccia utente.

-   **`activity_splash_screen.xml`**: Layout per `SplashScreenActivity`.
-   **`activity_welcome.xml`**: Layout per `WelcomeActivity`.
-   **`activity_main.xml`**: Layout per `MainActivity`.
-   **`fragment_home.xml`**: Layout per `HomeFragment`.
-   **`fragment_plant_list.xml`**: Layout per `PlantListFragment`.
-   **`fragment_plant_detail.xml`**: Layout per `PlantDetailFragment`.
-   **`dialog_aggiungi_pianta.xml`**: Layout per il dialog `mostraDialogAggiungiPianta()`.
-   **`fragment_user_plant.xml`**: Layout per `UserPlantFragment`.
-   **`fragment_profile.xml`**: Layout per `ProfileFragment`.
-   **`fragment_settings.xml`**: Layout per `SettingsFragment`.
-   **`fragment_annaffiature.xml`**: Layout per `AnnaffiatureFragment`.

### Drawable (`app/src/main/res/drawable/`)

Immagini, icone, e forme XML.

-   **`ic_plant_icon.xml`**: Icona dell'app o icona generica per le piante.
-   **`ic_add.xml`**: Icona per il pulsante "aggiungi".
-   **`background_gradient.xml`**: Eventuale drawable per uno sfondo personalizzato.

### Font (`app/src/main/res/font/`)

Font della applicazione.

-   **`inter_variablefont.ttf`**: Font dell'app.

### Values (`app/src/main/res/values/`)

File XML che definiscono valori costanti.

-   **`strings.xml`**: Tutte le stringhe di testo utilizzate nell'app (nome dell'app, icone, ecc).
    * Esempio: `app_name`, `home`, `profile`.
-   **`colors.xml`**: Colori utilizzati nell'app.
    * Esempio: `verde`.
-   **`themes.xml`**: Tema chiaro e scuro dell'app (colori, font, icone).
	- Esempio: `colorPrimary`, `android:fontFamily`.

### Menu (`app/src/main/res/menu/`)

File XML per definire i menu dell'app (es. option menu nella app bar).

-   **`main_menu.xml`**
-   **`bottom_nav_menu.xml`**

### Navigation (`app/src/main/res/navigation/`)

Grafi di navigazione

-   **`navigation_map.xml`**: Percorsi di navigazione tra le diverse schermate/fragment/destinazioni.

---

## File di Build (Gradle Scripts)

-   **`app/build.gradle`**:
    * *Descrizione:* Contiene le configurazioni specifiche del modulo `app`, come `applicationId`, `minSdkVersion`, `targetSdkVersion`, `versionCode`, `versionName`, e le **dipendenze** della libreria (es. Retrofit, Room, Glide, ecc.).
-   **`build.gradle` (a livello di progetto)**:
    * *Descrizione:* Contiene le configurazioni che si applicano a tutti i moduli del progetto, come le versioni dei plugin Gradle e i repository.
-   **`settings.gradle`**:
    * *Descrizione:* Definisce quali moduli sono inclusi nel progetto.

---

## Altri File Importanti

-   **`AndroidManifest.xml` (`app/src/main/`)**:
    * *Descrizione:* File fondamentale che descrive le componenti dell'applicazione (Activities, Services, BroadcastReceivers, ContentProviders), i permessi richiesti, le funzionalità hardware e software, ecc.
-   **`.gitignore`**:
    * *Descrizione:* Specifica i file e le cartelle che Git dovrebbe ignorare.
-   **`README.md`**: (Questo file)
    * *Descrizione:* Fornisce una panoramica generale del progetto.
