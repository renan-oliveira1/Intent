package edu.br.ifsp.scl.ads.pdm.intent

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import edu.br.ifsp.scl.ads.pdm.intent.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    companion object Constantes {
        val PARAMETRO = "PARAMETRO"
        val RETORNO = "RETORNO"
        val OUTRA_ACTIVITY_REQUEST_CODE = 0
        val RECEBER_RETORNAR_ACTION = "RECEBER_RETORNAR_ACTION"
    }

    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var outraActivityResultLaucher: ActivityResultLauncher<Intent>

    private lateinit var requisitarPermissaoArl: ActivityResultLauncher<String>

    private lateinit var selecionarImagemArl: ActivityResultLauncher<Intent>

    private lateinit var escolherAppArl: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(activityMainBinding.root)

        supportActionBar?.title = "Tratando Intents"
        supportActionBar?.subtitle = "Principais tipos"

        outraActivityResultLaucher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            object: ActivityResultCallback<ActivityResult> {
                override fun onActivityResult(result: ActivityResult?) {
                    if(result?.resultCode == RESULT_OK){
                        val retorno: String = result.data?.getStringExtra(RETORNO)?: ""
                        activityMainBinding.textReturTv.text = retorno
                    }
                }
        }
        )

        requisitarPermissaoArl = registerForActivityResult(
            ActivityResultContracts.RequestPermission())
            { permissaoConcedida ->
                if(permissaoConcedida) discarTelefone(ACTION_CALL)
                else{
                    Toast.makeText(this@MainActivity, "Permissão necessária", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        selecionarImagemArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
            if(resultado.resultCode == RESULT_OK){
                visualizarImagem(resultado)
            } else {
                Toast.makeText(this@MainActivity, "Imagem não selecionada", Toast.LENGTH_SHORT).show()
            }
        }

        escolherAppArl = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ resultado ->
            if(resultado.resultCode == RESULT_OK){
                visualizarImagem(resultado)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.outraActivityMi -> {

                val outraActivityIntent = Intent(RECEBER_RETORNAR_ACTION)

                val parametrosBundle = Bundle()
                val parametro: String = activityMainBinding.editParam.text.toString()
                parametrosBundle.putString(PARAMETRO, parametro)

                outraActivityIntent.putExtras(parametrosBundle)

                outraActivityResultLaucher.launch(outraActivityIntent)
                true
            }
            R.id.viewMi -> {

                val url = activityMainBinding.editParam.text.toString().let{
                    if(!it.lowercase().contains("http[s]?".toRegex())){
                        "https://${it}"
                    } else {
                        it
                    }
                }
                val siteIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                startActivity(siteIntent)
                true
            }
            R.id.callMi -> {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        discarTelefone(ACTION_CALL)
                    } else {
                        requisitarPermissaoLigacao()
                    }
                } else {
                    discarTelefone(ACTION_CALL)
                }

                true
            }
            R.id.dialMi -> {
                discarTelefone(ACTION_DIAL)
                true
            }
            R.id.pickMi-> {
                val pegarImagemIntent = Intent(ACTION_PICK)
                val diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path
                pegarImagemIntent.setDataAndType(Uri.parse(diretorio), "image/*")
                selecionarImagemArl.launch(pegarImagemIntent)
                true
            }
            R.id.chooserMi -> {
                val escolherAppIntent = Intent(ACTION_CHOOSER)
                val pegarImagenIntent = prepararPegarImagemIntent()
                escolherAppIntent.putExtra(EXTRA_INTENT, pegarImagenIntent)
                escolherAppIntent.putExtra(EXTRA_TITLE, "Escolha sua galeria")

                escolherAppArl.launch(escolherAppIntent)
                true
            }
            else -> false
        }

    }


    private fun discarTelefone(action: String){
        val numero = activityMainBinding.editParam.text.toString()
        val chamadaIntent = Intent(
            action,
            Uri.parse("tel: $numero")
        )
        startActivity(chamadaIntent)
    }

    private fun requisitarPermissaoLigacao(){
        requisitarPermissaoArl.launch(CALL_PHONE)
    }

    private fun visualizarImagem(resultado: ActivityResult){
        val caminhoUri: Uri? = resultado.data?.data
        val visualizarImagemIntent = Intent(
            ACTION_VIEW,
            caminhoUri
        )
        startActivity(visualizarImagemIntent)
    }

    private fun prepararPegarImagemIntent(): Intent {
        val pegarImagemIntent = Intent(ACTION_PICK)
        val diretorio = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.path
        pegarImagemIntent.setDataAndType(Uri.parse(diretorio), "image/*")
        return pegarImagemIntent
    }

}