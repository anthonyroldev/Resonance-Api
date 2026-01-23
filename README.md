# Resonance Music Library

Plateforme de catalogage musical ("Letterboxd for Music").
La web-app permet aux utilisateurs de construire et partager leur bibliothèque musicale et de découvrir de nouveaux artistes.

## Lancer le backend

Avec Docker Compose :
```
docker-compose up -d --build
```

Avec Gradle :
```
./gradlew bootRun
```

## Documentation de l'API

La documentation de l'API est disponible à l'adresse suivante une fois le backend lancé :
```
http://localhost:8080/swagger-ui/index.html
```

## Thème du Projet

Resonance est une application de gestion de bibliothèque musicale connectée. Elle permet aux utilisateurs de :

1. **Cataloguer** : Construire sa discothèque virtuelle via l'API ITunes (Albums, Sons, Artistes).
2. **Découvrir** : Explorer de nouveaux artistes et albums.

## Stack Technique

L'architecture repose sur une séparation Frontend/Backend conteneurisée.

### Backend (API REST)

* **Framework** : Spring Boot 4.
* **Langage** : Java 25.
* **Sécurité** : Spring Security + OAuth2 Client (Intégration Spotify & Google).
* **Base de données** : PostgreSQL.

### Frontend

* **Framework** : Next.js.
* **Langage** : TypeScript.
* **Styling** : Tailwind CSS + Shadcn UI pour les composants UI.

### Infrastructure

* **Conteneurisation** : Docker & Docker Compose (Services API, Frontend, Database).
