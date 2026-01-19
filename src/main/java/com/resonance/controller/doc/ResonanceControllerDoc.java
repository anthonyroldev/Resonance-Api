package com.resonance.controller.doc;

import com.resonance.config.OpenApiConfig;
import com.resonance.dto.audiodb.AudioDbArtistResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Schema(description = "Gestion des fonctionnalités principales de Resonance.")
public interface ResonanceControllerDoc {

    @Schema(description = "Recherche des albums d'un artiste par son nom.")
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    AudioDbArtistResponseDTO searchAlbum(String artistQuery);
}
