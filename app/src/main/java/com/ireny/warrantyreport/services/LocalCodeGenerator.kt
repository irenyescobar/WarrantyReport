package com.ireny.warrantyreport.services

import com.ireny.warrantyreport.services.interfaces.ICodeGenerator
import kotlin.random.Random

class LocalCodeGenerator:ICodeGenerator {

    override suspend fun generateNewCode(key_app: String): String {
        val code = Random.nextInt()
        return "INVALID${code.toString().padStart(4,'0')}"
    }
}