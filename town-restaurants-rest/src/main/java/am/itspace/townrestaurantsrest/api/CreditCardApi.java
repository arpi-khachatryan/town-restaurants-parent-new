package am.itspace.townrestaurantsrest.api;

import am.itspace.townrestaurantscommon.dto.creditCard.CreditCardOverview;
import am.itspace.townrestaurantscommon.dto.event.EventOverview;
import am.itspace.townrestaurantsrest.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface CreditCardApi {

    @Operation(
            summary = "Get all credit cards",
            description = "Possible error code: 4052")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Fetched events from DB",
                            content = @Content(
                                    schema = @Schema(implementation = EventOverview.class),
                                    mediaType = APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "4052",
                            description = "Credit card not found",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)))})
    ResponseEntity<List<CreditCardOverview>> getAll(int pageNo, int pageSize, String sortBy, String sortDir);

    @Operation(
            summary = "Get all credit cards",
            description = "Possible error codes: 4052, 4094")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Fetched events from DB",
                            content = @Content(
                                    schema = @Schema(implementation = EventOverview.class),
                                    mediaType = APPLICATION_JSON_VALUE)),
                    @ApiResponse(
                            responseCode = "4052",
                            description = "Credit card not found",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class))),
                    @ApiResponse(
                            responseCode = "4094",
                            description = "Needs to authenticate",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiError.class)))})
    ResponseEntity<List<CreditCardOverview>> getByUser(int pageNo, int pageSize, String sortBy, String sortDir);
}
