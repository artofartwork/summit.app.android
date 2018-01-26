package com.movil.summmit.motorresapp;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.movil.summmit.motorresapp.Models.Enity.InformeTecnico;
import com.movil.summmit.motorresapp.Models.Enity.InformeTecnicoAdjuntos;
import com.movil.summmit.motorresapp.Models.Enity.InformeTecnicoAdjuntosDetalle;
import com.movil.summmit.motorresapp.Storage.Files.FilesControl;
import com.movil.summmit.motorresapp.Storage.db.repository.InformeTecnicoAdjuntosDetalleRepository;
import com.movil.summmit.motorresapp.Storage.db.repository.InformeTecnicoAdjuntosRepository;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class AdjuntosActivity extends AppCompatActivity {

    LinearLayout layout;
    EditText edtVin, edtKmHoras;
    TextInputLayout tmpVin, tmpKmHoras;
    List<EditText> listaEditTextFiles;
    Integer inputFileSelected = 0;
    String fileVin = "";
    String fileKmHoras = "";
    FilesControl filesControl;
    String estacionPath;
    int IdInformeTecnico = 0;
    InformeTecnicoAdjuntosRepository informeTecnicoAdjuntosRepository;
    InformeTecnicoAdjuntosDetalleRepository informeTecnicoAdjuntosDetalleRepository;
    EditText edtGeneradoClikeado;
    Integer Seleccion = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjuntos);
        Intent myIntent = getIntent(); // gets the previously created intent
        IdInformeTecnico = myIntent.getIntExtra("IdInformeTecnico", 0);
        informeTecnicoAdjuntosRepository = new InformeTecnicoAdjuntosRepository(this);
        informeTecnicoAdjuntosDetalleRepository = new InformeTecnicoAdjuntosDetalleRepository(this);
        init();
        filesControl = new FilesControl();
        estacionPath =  filesControl.getAlbumStorageDirEstacion("INFORME_" + IdInformeTecnico) + File.separator; //+ "hola.txt";
    }

    public void init()
    {
        tmpVin = (TextInputLayout)findViewById(R.id.tmpVin);
        tmpKmHoras = (TextInputLayout)findViewById(R.id.tmpKmHoras);

        listaEditTextFiles = new ArrayList<>();
        edtVin = (EditText)findViewById(R.id.edtVin);
        edtVin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seleccion = 0;
                inputFileSelected = 1;
                openFileExplorer();
            }
        });
        edtKmHoras = (EditText)findViewById(R.id.edtKmHoras);
        edtKmHoras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seleccion = 0;
                inputFileSelected = 2;
                openFileExplorer();
            }
        });
        layout =(LinearLayout)findViewById(R.id.agregados);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        setTitle("Adjuntos");
        getMenuInflater().inflate(R.menu.menu_nuevo, menu);
        super.onCreateOptionsMenu(menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_save:

                if (validateForm())
                {
                    InformeTecnicoAdjuntos objMapper = mapperInformeTecnicoAdjuntos();
                    try
                    {
                        int IdGenerado = informeTecnicoAdjuntosRepository.create(objMapper);

                        // si todo sale bien se guardan los comentarios

                       List<InformeTecnicoAdjuntosDetalle> listaDetalle =   mapperAdjuntoDetalleLis();
                        if (IdGenerado > 0)
                        {
                            for (InformeTecnicoAdjuntosDetalle obj: listaDetalle)
                            {
                                obj.setIdAdjuntos(IdGenerado);
                                informeTecnicoAdjuntosDetalleRepository.create(obj);
                            }
                        }
                        Intent inte =new Intent(AdjuntosActivity.this, GuardarEnviarActivity.class);
                        inte.putExtra("IdInformeTecnico", IdInformeTecnico);
                        startActivity(inte);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(this, "OCURRIO UN ERRROR INESPERADO", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

        }

        return true;
    }

    public void agregaFiles(View view) {

        final EditText edt = new EditText(this);
        edt.setLayoutParams(layout.getLayoutParams());
        edt.setTextSize(10);
        edt.setHint("Seleccionar archivo");
        edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Seleccion = 1;
                edtGeneradoClikeado = edt;
                openFileExplorer();
            }
        });
        listaEditTextFiles.add(edt);
        layout.addView(edt);


    }

    private void openFileExplorer()
    {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(FilePickActivity.SUFFIX,
                new String[] {"xlsx", "xls", "doc", "dOcX", "ppt", ".pptx", "pdf"});
        startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
        NormalFile objnormal = list.get(0);

        String nombreArchivo = objnormal.getName();
        String path = objnormal.getPath();
        String filename =path.substring(path.lastIndexOf("/")+1);

        if (Seleccion == 0)
        {
            switch (inputFileSelected)
            {
                case 1:
                    fileVin = "vin_" + filename;
                    filename = "vin_" + filename;
                    edtVin.setText(filename.toString());
                    break;
                case 2:
                    fileKmHoras = "kmh_" + filename;
                    filename = "kmh_" + filename;
                    edtKmHoras.setText(filename.toString());
                    break;
            }
        }
        else if (Seleccion == 1)
        {
            filename = "det_" + filename;
            edtGeneradoClikeado.setText(path.toString());
        }

        File scrc = new File(path);
        String filefinal = estacionPath + filename;
        File dest = new File(filefinal);

        try
        {
            copy(scrc,dest );

        } catch (IOException e) {
            Toast.makeText(this, "ERROR EN CARGA DE ARCHIVOS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
    public Boolean validateForm(){


        if(edtVin.getText().toString().trim().isEmpty())
        {
            tmpVin.setError("Este campo es obligatorio");
            return false;
        }

        if(edtKmHoras.getText().toString().trim().isEmpty())
        {
            tmpKmHoras.setError("Este campo es obligatorio");
            return false;
        }

        for (EditText obj : listaEditTextFiles)
        {
            if (obj.getText().toString().isEmpty())
            {
                obj.setError("Este campo es obligatorio");
                return false;
            }
        }

        return true;
    }

    public InformeTecnicoAdjuntos mapperInformeTecnicoAdjuntos()
    {
        InformeTecnicoAdjuntos objInforme = new InformeTecnicoAdjuntos();
        objInforme.setIdInformeTecnico(IdInformeTecnico);
        objInforme.setArchivoNombreVin(fileVin);
        objInforme.setArchivoNombreKm(fileKmHoras);

        return  objInforme;
    }

    public List<InformeTecnicoAdjuntosDetalle>  mapperAdjuntoDetalleLis(){

        List<InformeTecnicoAdjuntosDetalle> lista = new ArrayList<>();

        for (EditText objEd : listaEditTextFiles)
        {
            InformeTecnicoAdjuntosDetalle objDet = new InformeTecnicoAdjuntosDetalle();
            objDet.setIdInformeTecnico(IdInformeTecnico);
            objDet.setArchivoNombre(objEd.getText().toString().trim());
            lista.add(objDet);
        }

        return lista;

    }
}
