package br.ordnax.portariadigital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.ordnax.portariadigital.data.model.AcessoDto
import br.ordnax.portariadigital.ui.viewmodel.AcessosUiState
import br.ordnax.portariadigital.ui.viewmodel.AcessosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcessosScreen(
    modifier: Modifier = Modifier,
    viewModel: AcessosViewModel = viewModel(),
    onDispararPanico: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDialogAutorizar by remember { mutableStateOf(false) }
    var nomeVisitante by remember { mutableStateOf("") }
    var cpfVisitante by remember { mutableStateOf("") }
    var unidadeVisitante by remember { mutableStateOf("") }
    var placaVisitante by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Portaria Digital • Acessos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    // Botão Recarregar lista
                    IconButton(onClick = { viewModel.carregarAcessos() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recarregar Acessos"
                        )
                    }

                    // Botão Discreto de Pânico Silencioso (Dia 4)
                    IconButton(onClick = {
                        onDispararPanico()
                        viewModel.dispararPanico { sucesso, msg ->
                            scope.launch {
                                if (sucesso) {
                                    snackbarHostState.showSnackbar("🚨 Alerta de pânico silencioso acionado!")
                                } else {
                                    snackbarHostState.showSnackbar("Falha no alerta: $msg")
                                }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta de Emergência",
                            tint = Color.Red.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialogAutorizar = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Liberar Visitante")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is AcessosUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is AcessosUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.mensagem,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = { viewModel.carregarAcessos() }) {
                            Text("Tentar Novamente")
                        }
                    }
                }
                is AcessosUiState.Success -> {
                    if (state.acessos.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Nenhum acesso registrado no momento.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.acessos, key = { it.uuid }) { item ->
                                ItemAcessoCard(item)
                            }
                        }
                    }
                }
            }
        }

        // Modal para autorizar novo visitante
        if (showDialogAutorizar) {
            AlertDialog(
                onDismissRequest = { showDialogAutorizar = false },
                title = { Text("Autorizar Visitante / Delivery") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nomeVisitante,
                            onValueChange = { nomeVisitante = it },
                            label = { Text("Nome Completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = cpfVisitante,
                            onValueChange = { cpfVisitante = it },
                            label = { Text("Documento / CPF") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = unidadeVisitante,
                            onValueChange = { unidadeVisitante = it },
                            label = { Text("Unidade (Ex: Apto 101)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = placaVisitante,
                            onValueChange = { placaVisitante = it },
                            label = { Text("Placa Veículo (Opcional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (nomeVisitante.isNotBlank() && unidadeVisitante.isNotBlank()) {
                                viewModel.autorizarVisitante(
                                    nome = nomeVisitante,
                                    cpf = cpfVisitante.ifBlank { "000.000.000-00" },
                                    unidade = unidadeVisitante,
                                    veiculoPlaca = placaVisitante.ifBlank { null }
                                ) { sucesso, msg ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(msg)
                                    }
                                }
                                showDialogAutorizar = false
                                nomeVisitante = ""
                                cpfVisitante = ""
                                unidadeVisitante = ""
                                placaVisitante = ""
                            }
                        }
                    ) {
                        Text("Autorizar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogAutorizar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ItemAcessoCard(acesso: AcessoDto) {
    Card(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconVector = when (acesso.tipoAcesso.lowercase()) {
                "delivery" -> Icons.Default.LocalShipping
                "veiculo", "carro" -> Icons.Default.DirectionsCar
                else -> Icons.Default.Person
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(23.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = acesso.tipoAcesso,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = acesso.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${acesso.tipoAcesso} • ${acesso.unidade}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                if (!acesso.veiculoPlaca.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Placa: ${acesso.veiculoPlaca}",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            val isAutorizado = acesso.status.equals("Autorizado", ignoreCase = true)
            Surface(
                color = if (isAutorizado) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = acesso.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = if (isAutorizado) Color(0xFF2E7D32) else Color(0xFFE65100),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}