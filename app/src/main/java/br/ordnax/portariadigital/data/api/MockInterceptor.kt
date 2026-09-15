package br.ordnax.portariadigital.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()
        val method = chain.request().method

        val (responseString, responseCode) = when {
            uri.endsWith("token/") -> Pair(
                """{"token":"mock_token_123456","user_id":1,"nome":"Porteiro Demo","tipo":"Porteiro","condominio":"Residencial GitHub"}""",
                200
            )
            uri.contains("acessos/autorizar/") -> Pair(
                """{"uuid":"mock-novo-${System.currentTimeMillis()}","mensagem":"Entrada autorizada com sucesso!","status":"Autorizado"}""",
                201
            )
            uri.contains("acessos/saida/") -> Pair(
                """{"mensagem":"Saída registrada com sucesso!","data_saida":"2026-09-14T20:15:22Z"}""",
                200
            )
            uri.contains("acessos/recentes/") -> Pair(
                """[
                    {"uuid":"1","nome":"Entregador iFood","tipo_acesso":"Delivery","unidade":"Apto 204","veiculo_placa":"MTO-1234","status":"Autorizado","data_entrada":"2026-09-14T19:40:00Z","data_saida":null},
                    {"uuid":"2","nome":"Ana Paula (Visitante)","tipo_acesso":"Visitante","unidade":"Apto 101","veiculo_placa":null,"status":"Autorizado","data_entrada":"2026-09-14T18:10:00Z","data_saida":null},
                    {"uuid":"3","nome":"Carlos Manutenção","tipo_acesso":"Prestador","unidade":"Área Comum","veiculo_placa":"ABC-9876","status":"Pendente","data_entrada":"2026-09-14T17:30:00Z","data_saida":null}
                ]""",
                200
            )
            uri.contains("correspondencias/pendentes/") -> Pair(
                """[
                    {"uuid":"c1","nome_morador":"Felipe Dev","unidade":"502","torre":"Torre 1","codigo_rastreio":"ML99887766BR","origem":"Transportadora","tipo_pacote":"Caixa","status":"Pendente","hash_entrega":"7X9A21","data_recebimento":"2026-09-14T16:00:00Z"}
                ]""",
                200
            )
            uri.contains("correspondencias/") && uri.contains("/entregar/") -> Pair(
                """{"mensagem":"Entrega confirmada com sucesso!","status":"Entregue","data_entrega":"2026-09-14T19:50:12Z"}""",
                200
            )
            uri.contains("correspondencias/") && method == "POST" -> Pair(
                """{"uuid":"mock-corresp-1","mensagem":"Correspondência cadastrada! Notificação enviada ao morador.","hash_entrega":"K7M9P2"}""",
                201
            )
            uri.contains("operacional/panico/") -> Pair(
                """{"status":"Alerta disparado com sucesso","codigo_ocorrencia":"PANIC-TESTE-OK","administracao_notificada":true}""",
                200
            )
            else -> Pair("""{"mensagem":"Mock OK"}""", 200)
        }

        return Response.Builder()
            .code(responseCode)
            .message("Mock Response")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(responseString.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }
}