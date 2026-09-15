package br.ordnax.portariadigital.data.api

import br.ordnax.portariadigital.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface PortariaApiService {

    @POST("api/v1/token/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @GET("api/v1/acessos/recentes/")
    suspend fun getAcessosRecentes(): Response<List<AcessoDto>>

    @POST("api/v1/acessos/autorizar/")
    suspend fun autorizarAcesso(
        @Body request: AutorizarAcessoRequest
    ): Response<AcessoResponse>

    @POST("api/v1/acessos/saida/{uuid}/")
    suspend fun registrarSaida(
        @Path("uuid") uuid: String
    ): Response<RegistrarSaidaResponse>

    @GET("api/v1/correspondencias/pendentes/")
    suspend fun getCorrespondenciasPendentes(): Response<List<CorrespondenciaDto>>

    @POST("api/v1/correspondencias/")
    suspend fun cadastrarCorrespondencia(
        @Body request: NovaCorrespondenciaRequest
    ): Response<NovaCorrespondenciaResponse>

    @POST("api/v1/correspondencias/{uuid}/entregar/")
    suspend fun darBaixaCorrespondencia(
        @Path("uuid") uuid: String,
        @Body request: BaixaCorrespondenciaRequest
    ): Response<BaixaCorrespondenciaResponse>

    @POST("api/v1/operacional/panico/")
    suspend fun dispararPanicoSilencioso(
        @Body request: AlertaPanicoRequest
    ): Response<AlertaPanicoResponse>




}