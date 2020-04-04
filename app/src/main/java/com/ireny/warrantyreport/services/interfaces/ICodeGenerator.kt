package com.ireny.warrantyreport.services.interfaces

interface ICodeGenerator {
   suspend fun generateNewCode(key_app: String):String
}