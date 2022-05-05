package edu.br.ifsp.scl.ads.pdm.intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.br.ifsp.scl.ads.pdm.intent.MainActivity.Constantes.PARAMETRO
import edu.br.ifsp.scl.ads.pdm.intent.MainActivity.Constantes.RETORNO
import edu.br.ifsp.scl.ads.pdm.intent.databinding.ActivityOutraBinding

class OutraActivity : AppCompatActivity() {

    private val activityOutraBinding: ActivityOutraBinding by lazy {
        ActivityOutraBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityOutraBinding.root)

        supportActionBar?.title = "Outra Activity"
        supportActionBar?.subtitle = "Recebe e retorna um valor"

        val intent = intent
        val parametrosBundle: Bundle? = intent.extras
        if(parametrosBundle != null){
            val recebido: String = parametrosBundle.getString(PARAMETRO)?:""
            activityOutraBinding.textTv.text = recebido
        }

        activityOutraBinding.btnReturn.setOnClickListener{
            val retornoIntent: Intent = Intent()
            val retorno = activityOutraBinding.editReturnEt.text.toString()
            retornoIntent.putExtra(RETORNO, retorno)
            setResult(RESULT_OK, retornoIntent)
            finish()
        }

    }
}