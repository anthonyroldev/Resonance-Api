# Resonance Music Library

Plateforme de catalogage musical ("Letterboxd for Music").
La web-app permet aux utilisateurs de construire et partager leur bibliothèque musicale, découvrir de nouveaux artistes et se connecter avec d'autres passionnés de musique.

## Thème du Projet

Resonance est une application de gestion de bibliothèque musicale connectée. Elle permet aux utilisateurs de :

1. **Cataloguer** : Construire sa discothèque virtuelle via l'API Spotify (Albums, Sons, Artistes).
2. **Socialiser** : Découvrir ses "âmes sœurs musicales" grâce à un algorithme de compatibilité (Matching) basé sur les goûts communs stockés en base de données.
3. **Explorer** : Centraliser ses écoutes et découvertes issues de multiples sources (Spotify, YouTube Music, etc.) via une authentification unifiée.

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
