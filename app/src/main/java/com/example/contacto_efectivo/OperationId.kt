package com.example.contacto_efectivo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class OperationsViewModel: ViewModel() {
    var operationId = mutableStateOf<String?>(null)
    var operationIdUrl = mutableStateOf<String?>(null)
    var thirdOperationInCourse = mutableStateOf<Boolean?>(null)
    var statusFlowTable = mutableStateOf<String?>(null)
}