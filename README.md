# Plantminder - Documentazione

<center>
	<img src="https://github.com/zmplc/plantminder/blob/main/app/src/main/ic_plantminder_icona-playstore.png" alt="Plantminder Logo" style="width:50%; height:auto;">
</center>

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
    * *Descrizione:* Fragment per i dettagli della pianta scelta dalla lista. Viene mostrato il nome, il nome scientifico, la descrizione, la temperatura, la luce e l'irrigazione richiesta per la pianta.
-   **`GardenPlantDetailFragment.kt`**:
    * *Descrizione:* Fragment che mostra il dettaglio delle innaffiature (comprese quelle passate), i bottoni per segnare la pianta come annaffiata, rinominarla ed eliminarla.
-   **`ProfileFragment.kt`**:
    * *Descrizione:* Fragment per mostrare il profilo dell'utente (nome, email, piante salvate), modificare la password ed effettuare il logout.
-   **`SettingsFragment.kt`**:
    * *Descrizione:* Fragment dove l'utente può modificare il tema dell'app (chiaro/scuro), attivare/disattivare le notifiche, cancellare tutte le piante dal suo giardino e un bottone che lo porta alla documentazione.

### Adapters

Gli Adapters collegano i dati (ad esempio l'elenco delle piante) a viste dell'interfaccia utente come `RecyclerView`.

-   **`PlantAdapter.kt`**:
    * *Descrizione:* Gestisce la visualizzazione dell'elenco delle piante, mostrate in `PlantListFragment`, salvate nel database che l'utente può aggiungere al suo giardino.
-   **`GardenPlantsAdapter.kt`**:
    * *Descrizione:* Gestisce la visualizzazione dell'elenco delle piante, mostrate in `HomeFragment`, salvate nel giardino dell'utente.
-   **`AnnaffiatureAdapter.kt`**:
    * *Descrizione:* Gestisce la visualizzazione dell'elenco delle piante, mostrate in `InnaffiatureFragment`, salvate nel giardino dell'utente.

### Models (Data Classes)

Classi che rappresentano la struttura dei dati utilizzati nell'app.

-   **`Plant.kt`**:
    * *Descrizione:* Classe dati per le piante (id, nome, nomeScientifico, descrizione, tipo, temperatura, luce, acqua).
-   **`GardenPlant.kt`**:
    * *Descrizione:* Classe dati per le piante salvate dall'utente  (id, nomePersonalizzato, nome).
-   **`InfoInnaffiatura.kt`**:
    * *Descrizione:* Classe dati per gestire le annaffiature.

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
-   **`fragment_profile.xml`**: Layout per `ProfileFragment`.
-   **`fragment_settings.xml`**: Layout per `SettingsFragment`.
-   **`fragment_garden_plant_detail.xml`**: Layout per `GardenPlantDetailFragment`.

### Drawable (`app/src/main/res/drawable/`)

Immagini, icone e forme XML.

-   **`blur_background.jpg`**: Immagine sfuocata utilizzata come sfondo per `WelcomeActivity`.
-   **`ic_add_white.xml`**: Icona per il pulsante "aggiungi".
-   **`ic_aiuto.xml`**: Icona per il pulsante Documentazione.
-   **`ic_arrow_back.xml`**: Icona per tornare alla schermata precedente (tema chiaro).
-   **`ic_arrow_back_dark.xml`**: Icona per tornare alla schermata precedente (tema scuro).
-   **`ic_arrow_forward.xml`**: Icona per passare alla schermata successiva (tema chiaro).
-   **`ic_arrow_forward_dark.xml`**: Icona per passare alla schermata successiva (tema scuro).
-   **`ic_calendar.xml`**: Icona calendario (tema chiaro).
-   **`ic_calendar_dark.xml`**: Icona calendario (tema scuro).
-   **`ic_cambiopassword.xml`**: Icona per il pulsante Cambio password.
-   **`ic_check_circle_white.xml`**: Icona per il pulsante Annaffia pianta.
-   **`ic_elimina.xml`**: Icona per i pulsanti eliminazione.
-   **`ic_home.xml`**: Icona home (tema chiaro).
-   **`ic_home_dark.xml`**: Icona home (tema scuro).
-   **`ic_launcher_background.xml`**
-   **`ic_launcher_foreground.xml`**
-   **`ic_light.xml`**: Icona per campo luce in `PlantDetailFragment`.
-   **`ic_logout.xml`**: Icona per il pulsante Logout.
-   **`ic_modifica.xml`**: Icona per il pulsante Rinomina.
-   **`ic_plantminder_icona_foreground.xml`**
-   **`ic_potted_plant.xml`**: Icona potted plant (tema chiaro).
-   **`ic_potted_plant_dark.xml`**: Icona potted plant (tema scuro).
-   **`ic_profile.xml`**: Icona profilo (tema chiaro).
-   **`ic_profile_dark.xml`**: Icona profilo (tema scuro).
-   **`ic_temperature.xml`**: Icona per campo temperatura in `PlantDetailFragment`.
-   **`ic_water.xml`**: Icona per campo annaffiatura in `PlantDetailFragment`.
-   **`plantminder_logo.png`**: Icona Plantminder.
-   **`rounded_button.xml`**: Forma per pulsante Inizia in `WelcomeActivity`.
-   **`splash_screen.jpg`**: Immagine di sfondo per `SplashScreenActivity`.
-   **`status_circle.xml`**: Pallino per stato annaffiautra della pianta (mai annaffiata).
-   **`status_circle_blue.xml`**: Pallino per stato annaffiautra della pianta (da annaffiare).
-   **`status_circle_green.xml`**: Pallino per stato annaffiautra della pianta (annaffiata).
-   **`status_circle_red.xml`**: Pallino per stato annaffiautra della pianta (non annaffiata/in ritardo).

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

-   **`app/build.gradle`**
-   **`build.gradle` (project level)**
-   **`settings.gradle`**

---

## Altri File Importanti

-   **`AndroidManifest.xml` (`app/src/main/`)**:
    * *Descrizione:* File fondamentale che descrive le componenti dell'applicazione (Activities, Services, BroadcastReceivers, ContentProviders), i permessi richiesti, le funzionalità hardware e software, ecc.
-   **`.gitignore`**:
    * *Descrizione:* Specifica i file e le cartelle che Git dovrebbe ignorare.
-   **`README.md`**: (Questo file)
    * *Descrizione:* Fornisce una panoramica generale del progetto.
