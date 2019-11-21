package com.ireny.warrantyreport.services.interfaces

interface ICodeGenerator {
   suspend fun generateNewCode():String
}