package com.movil.summmit.motorresapp.Storage.db.repository;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.movil.summmit.motorresapp.Models.Enity.InformeTecnicoFalla;
import com.movil.summmit.motorresapp.Storage.db.DatabaseHelper;
import com.movil.summmit.motorresapp.Storage.db.manager.DatabaseManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cgonzalez on 17/01/2018.
 */

public class InformeTecnicoFallaRepository {

    private DatabaseHelper dbHelper;
    private Dao<InformeTecnicoFalla, Integer> entidadDao;

    public InformeTecnicoFallaRepository(Context context) {
        DatabaseManager dbManager = new DatabaseManager();
        dbHelper = dbManager.getHelper(context);
        try {
            entidadDao = dbHelper.getInformeTecnicoFallaDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int create(InformeTecnicoFalla entidad) {
        try {
            entidadDao.create(entidad);

            return entidad.getIdInformeTecnicoFalla();
//            dbHelper.getDatab
        } catch (SQLException e) {
//            e.printStackTrace();
            return 0;
        }
        //
    }

    public int update(InformeTecnicoFalla entidad) {
        try {
            return entidadDao.update(entidad);
        } catch (SQLException e) {
            e.printStackTrace();
            //Log.v(TAG, "update exception " + e);
        }

        return 0;
    }

    public int delete(InformeTecnicoFalla entidad) {
        try {
            return entidadDao.delete(entidad);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public InformeTecnicoFalla getById(int id) {
        try {
            return entidadDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<InformeTecnicoFalla> findAllxInforme(int IdInforme) {
        try {

            List<InformeTecnicoFalla> listaInforme = new ArrayList<>();
            GenericRawResults<String[]> rawResults =
                    entidadDao.queryRaw(
                            "select " +
                                    "sistema.Nombre as NombreSistema, "+
                                    "modo.Nombre as NombreModoFalla, "+
                                    "inf.NroCaso, " +
                                    "inf.Scanner, " +
                                    "inf.Combustible, " +
                                    "inf.Aceite " +
                                    "from informetecnicofalla as inf " +
                                    "inner join maestraargu as modo on modo.IdArgu = inf.IdArguModoFalla "+
                                    "inner join maestraargu as sistema on sistema.IdArgu = inf.IdArguSistema where inf.IdInformeTecnico = " + IdInforme

                    );

            List<String[]> results = rawResults.getResults();

            for (String[] result : results) {

                InformeTecnicoFalla objInforme = new InformeTecnicoFalla();
                objInforme.setNombreSistema(result[0]);
                objInforme.setNombreModoFalla(result[1]);
                objInforme.setNroCaso(result[2]);
                objInforme.setScanner( result[3].equalsIgnoreCase("1") ? true : false);
                objInforme.setCombustible( result[4].equalsIgnoreCase("1") ? true : false);
                objInforme.setAceite( result[5].equalsIgnoreCase("1") ? true :  false);
                listaInforme.add(objInforme);
            }

            return  listaInforme;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public long getNumberOfNotes() {
        QueryBuilder<InformeTecnicoFalla, Integer> qb = entidadDao.queryBuilder();
        try {
            return qb.countOf();
        } catch (SQLException e) {
            return -1;
        }
    }
}
