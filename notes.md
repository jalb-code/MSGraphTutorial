# MSGraphTutorial

## Lien du tutorial

https://docs.microsoft.com/fr-fr/graph/tutorials/java?tabs=aad

### Procédure simplifiée

- Enregistrer une application sur Microsoft Identity Platform (Anc. Azure Active Directory) (Ne pas mettre d'URL de redirection)
- Copier l'ID application (**app.clientId**) et l'ID de l'annuaire (**app.authTenant**)
- Sur le poste
    ```
    gradle init --dsl groovy --test-framework junit --type java-application --project-name graphtutorial --package graphtutorial
    ./gradlew --console plain run
    ```
- Ajouter les dépendances à Microsoft Identity (com.azure:azure-identity) et Microsoft Graph (com.microsoft.graph:microsoft-graph) dans app/build.gradle
- Créer le fichier oAuth.properties (Voir Modèle oAuout.properties.template) et renseigner **app.clientId** et **app.authTenant**
- Copier le code source comme indiqué dans le tutorial