package br.ordnax.portariadigital.data.model

import com.google.gson.annotations.SerializedName

// --- 1. AUTENTICAÇÃO ---
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("condominio") val condominio: String
)

// --- 2. CONTROLE DE ACESSO ---
data class AcessoDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("tipo_acesso") val tipoAcesso: String,
    @SerializedName("unidade") val unidade: String,
    @SerializedName("veiculo_placa") val veiculoPlaca: String? = null,
    @SerializedName("status") val status: String,
    @SerializedName("data_entrada") val dataEntrada: String = "",
    @SerializedName("data_saida") val dataSaida: String? = null
) {
    // Acessores para compatibilidade
    val data_entrada: String get() = dataEntrada
    val data_saida: String? get() = dataSaida
}

data class AutorizarAcessoRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("documento_cpf") val documentoCpf: String,
    @SerializedName("unidade") val unidade: String,
    @SerializedName("tipo_pessoa") val tipoPessoa: String = "Visitante",
    @SerializedName("veiculo_placa") val veiculoPlaca: String? = null,
    @SerializedName("observacao") val observacao: String? = null
) {
    val documento_cpf: String get() = documentoCpf
}

data class AcessoResponse(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("status") val status: String
)

data class RegistrarSaidaResponse(
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("data_saida") val dataSaida: String
)

// --- 3. CORRESPONDÊNCIA & ENCOMENDAS ---
data class CorrespondenciaDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("nome_morador") val nomeMorador: String,
    @SerializedName("unidade") val unidade: String,
    @SerializedName("torre") val torre: String,
    @SerializedName("codigo_rastreio") val codigoRastreio: String,
    @SerializedName("origem") val origem: String,
    @SerializedName("tipo_pacote") val tipoPacote: String,
    @SerializedName("status") val status: String,
    @SerializedName("hash_entrega") val hashEntrega: String?,
    @SerializedName("data_recebimento") val dataRecebimento: String
)

data class NovaCorrespondenciaRequest(
    @SerializedName("nome_morador") val nomeMorador: String,
    @SerializedName("unidade") val unidade: String,
    @SerializedName("torre") val torre: String = "Única",
    @SerializedName("codigo_rastreio") val codigoRastreio: String,
    @SerializedName("origem") val origem: String = "Correios",
    @SerializedName("tipo_pacote") val tipoPacote: String = "Caixa",
    @SerializedName("observacao") val observacao: String? = null
)

data class NovaCorrespondenciaResponse(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("hash_entrega") val hashEntrega: String
)

data class BaixaCorrespondenciaRequest(
    @SerializedName("recebedor") val recebedor: String,
    @SerializedName("hash_confirmacao") val hashConfirmacao: String
)

data class BaixaCorrespondenciaResponse(
    @SerializedName("mensagem") val mensagem: String,
    @SerializedName("status") val status: String,
    @SerializedName("data_entrega") val dataEntrega: String
)

// --- 4. OPERACIONAL & PÂNICO SILENCIOSO ---
data class AlertaPanicoRequest(
    @SerializedName("origem") val origem: String = "App Android",
    @SerializedName("dispositivo_id") val dispositivoId: String,
    @SerializedName("observacao") val observacao: String = "Acionamento Silencioso"
)

data class AlertaPanicoResponse(
    @SerializedName("status") val status: String,
    @SerializedName("codigo_ocorrencia") val codigoOcorrencia: String,
    @SerializedName("administracao_notificada") val administracaoNotificada: Boolean
)
