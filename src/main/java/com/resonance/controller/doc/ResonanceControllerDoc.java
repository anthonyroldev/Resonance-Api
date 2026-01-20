package com.resonance.controller.doc;

import com.resonance.config.OpenApiConfig;
import com.resonance.dto.library.AddToLibraryRequest;
import com.resonance.dto.library.LibraryEntryResponse;
import com.resonance.dto.library.UpdateLibraryEntryRequest;
import com.resonance.dto.media.MediaResponse;
import com.resonance.dto.media.SearchResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Resonance", description = "API principale pour la recherche musicale et la gestion de bibliotheque utilisateur.")
public interface ResonanceControllerDoc {

    // ==================== SEARCH ENDPOINTS ====================

    @Operation(
            summary = "Rechercher des albums",
            description = "Recherche des albums par nom. Les resultats sont caches localement pour optimiser les appels API externes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recherche effectuee avec succes"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchAlbums(
            @Parameter(description = "Terme de recherche pour l'album", example = "Homework")
            @RequestParam String q
    );

    @Operation(
            summary = "Rechercher des artistes",
            description = "Recherche des artistes par nom. Les resultats sont caches localement pour optimiser les appels API externes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recherche effectuee avec succes"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchArtists(
            @Parameter(description = "Terme de recherche pour l'artiste", example = "Daft Punk")
            @RequestParam String q
    );

    @Operation(
            summary = "Rechercher des pistes",
            description = "Recherche des pistes par terme de recherche via iTunes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recherche effectuee avec succes"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    ResponseEntity<SearchResponse<MediaResponse>> searchTracks(
            @Parameter(description = "Terme de recherche pour la piste", example = "Around the World")
            @RequestParam String q
    );

    // ==================== GET BY ID ENDPOINTS ====================

    @Operation(
            summary = "Obtenir un album par ID",
            description = "Recupere les details d'un album par son ID iTunes. Utilise le cache local si disponible."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album trouve avec succes"),
            @ApiResponse(responseCode = "404", description = "Album non trouve")
    })
    ResponseEntity<MediaResponse> getAlbumById(
            @Parameter(description = "ID iTunes de l'album", example = "1440857781")
            @PathVariable String id
    );

    @Operation(
            summary = "Obtenir un artiste par ID",
            description = "Recupere les details d'un artiste par son ID iTunes. Utilise le cache local si disponible."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Artiste trouve avec succes"),
            @ApiResponse(responseCode = "404", description = "Artiste non trouve")
    })
    ResponseEntity<MediaResponse> getArtistById(
            @Parameter(description = "ID iTunes de l'artiste", example = "5468295")
            @PathVariable String id
    );

    @Operation(
            summary = "Obtenir une piste par ID",
            description = "Recupere les details d'une piste par son ID iTunes. Utilise le cache local si disponible."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Piste trouvee avec succes"),
            @ApiResponse(responseCode = "404", description = "Piste non trouvee")
    })
    ResponseEntity<MediaResponse> getTrackById(
            @Parameter(description = "ID iTunes de la piste", example = "1440857786")
            @PathVariable String id
    );

    // ==================== LIBRARY ENDPOINTS ====================

    @Operation(
            summary = "Ajouter a la bibliotheque",
            description = "Ajoute un media (album, artiste, piste) a la bibliotheque de l'utilisateur avec optionnellement une note, un favori, et un commentaire."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Media ajoute a la bibliotheque avec succes"),
            @ApiResponse(responseCode = "400", description = "Requete invalide"),
            @ApiResponse(responseCode = "401", description = "Non authentifie")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<LibraryEntryResponse> addToLibrary(
            @RequestBody AddToLibraryRequest request,
            Principal principal
    );

    @Operation(
            summary = "Mettre a jour une entree de bibliotheque",
            description = "Met a jour la note, le statut favori, ou le commentaire d'une entree existante dans la bibliotheque."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entree mise a jour avec succes"),
            @ApiResponse(responseCode = "404", description = "Entree non trouvee"),
            @ApiResponse(responseCode = "401", description = "Non authentifie"),
            @ApiResponse(responseCode = "403", description = "Non autorise a modifier cette entree")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<LibraryEntryResponse> updateLibraryEntry(
            @Parameter(description = "ID de l'entree de bibliotheque")
            @PathVariable UUID entryId,
            @RequestBody UpdateLibraryEntryRequest request,
            Principal principal
    );

    @Operation(
            summary = "Supprimer de la bibliotheque",
            description = "Supprime un media de la bibliotheque de l'utilisateur."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Media supprime de la bibliotheque avec succes"),
            @ApiResponse(responseCode = "404", description = "Entree non trouvee"),
            @ApiResponse(responseCode = "401", description = "Non authentifie")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<Void> removeFromLibrary(
            @Parameter(description = "ID iTunes du media a supprimer")
            @PathVariable String mediaId,
            Principal principal
    );

    @Operation(
            summary = "Obtenir la bibliotheque de l'utilisateur",
            description = "Recupere toutes les entrees de la bibliotheque de l'utilisateur connecte."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bibliotheque recuperee avec succes"),
            @ApiResponse(responseCode = "401", description = "Non authentifie")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<List<LibraryEntryResponse>> getUserLibrary(Principal principal);

    @Operation(
            summary = "Obtenir les favoris de l'utilisateur",
            description = "Recupere tous les medias marques comme favoris par l'utilisateur connecte."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoris recuperes avec succes"),
            @ApiResponse(responseCode = "401", description = "Non authentifie")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<List<LibraryEntryResponse>> getUserFavorites(Principal principal);

    @Operation(
            summary = "Obtenir une entree de bibliotheque",
            description = "Recupere les details d'une entree specifique de la bibliotheque pour un media donne."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entree trouvee avec succes"),
            @ApiResponse(responseCode = "404", description = "Entree non trouvee"),
            @ApiResponse(responseCode = "401", description = "Non authentifie")
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    ResponseEntity<LibraryEntryResponse> getLibraryEntry(
            @Parameter(description = "ID iTunes du media")
            @PathVariable String mediaId,
            Principal principal
    );
}
