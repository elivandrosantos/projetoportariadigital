package br.ordnax.portariadigital.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ordnax.portariadigital.data.api.RetrofitClient
import br.ordnax.portariadigital.data.model.AcessoDto
import br.ordnax.portariadigital.data.model.AlertaPanicoRequest
import br.ordnax.portariadigital.data.model.AutorizarAcessoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AcessosUiState {
    data object Loading : AcessosUiState
    data class Success(val acessos: List<AcessoDto>) : AcessosUiState
    data class Error(val mensagem: String) : AcessosUiState
}

class AcessosViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow<AcessosUiState>(AcessosUiState.Loading)
    val uiState: StateFlow<AcessosUiState> = _uiState.asStateFlow()

    // Propriedade para compatibilidade com código existente
    val uiSate: StateFlow<AcessosUiState> get() = uiState

    init {
        carregarAcessos()
    }

    fun carregarAcessos() {
        viewModelScope.launch {
            _uiState.value = AcessosUiState.Loading

            try {
                val response = api.getAcessosRecentes()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = AcessosUiState.Success(response.body()!!)
                } else {
                    _uiState.value = AcessosUiState.Error("Falha na API: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = AcessosUiState.Error(e.localizedMessage ?: "Erro de conexão")
            }
        }
    }

    fun autorizarVisitante(
        nome: String,
        cpf: String,
        unidade: String,
        veiculoPlaca: String? = null,
        onResultado: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                val req = AutorizarAcessoRequest(
                    nome = nome,
                    documentoCpf = cpf,
                    unidade = unidade,
                    veiculoPlaca = veiculoPlaca
                )
                val response = api.autorizarAcesso(req)
                if (response.isSuccessful) {
                    onResultado(true, response.body()?.mensagem ?: "Entrada autorizada com sucesso!")
                    carregarAcessos()
                } else {
                    onResultado(false, "Erro ao autorizar: ${response.code()}")
                }
            } catch (e: Exception) {
                onResultado(false, e.localizedMessage ?: "Erro de conexão")
            }
        }
    }

    fun dispararPanico(
        dispositivoId: String = "Terminal_01",
        observacao: String = "Acionamento Silencioso",
        onResultado: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                val req = AlertaPanicoRequest(
                    origem = "App Portaria Android",
                    dispositivoId = dispositivoId,
                    observacao = observacao
                )
                val response = api.dispararPanicoSilencioso(req)
                if (response.isSuccessful && response.body() != null) {
                    onResultado(true, response.body()!!.status)
                } else {
                    onResultado(false, "Erro ao registrar pânico: ${response.code()}")
                }
            } catch (e: Exception) {
                onResultado(false, e.localizedMessage ?: "Erro de conexão")
            }
        }
    }
}

typealias AcessoViewModel = AcessosViewModel