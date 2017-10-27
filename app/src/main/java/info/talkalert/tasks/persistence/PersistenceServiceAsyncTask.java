package info.talkalert.tasks.persistence;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import info.talkalert.data.PersistenceService;

public class PersistenceServiceAsyncTask<T> extends AsyncTask<PersistenceServiceAsyncTask.CrudData,PersistenceServiceAsyncTask.CrudData,PersistenceServiceAsyncTask.CrudData> {

    private PersistenceService<T> persistanceService;

    private OnCreateTaskEnd onCreateTaskEndHandler;
    private OnGetAllTaskEnd onGetAllTaskEndHandler;
    private OnUpdateTaskEnd onUpdateTaskEndHandler;
    private OnDeleteTaskEnd onDeleteTaskEndHandler;
    private OnSaveTaskEnd onSaveTaskEndHandler;

    public enum OPERATION_TYPE { GET_ALL,CREATE,UPDATE,SAVE,DELETE,DELETE_LIST,READ}

    public PersistenceServiceAsyncTask(PersistenceService<T> persistanceService, Object caller) {
        this.persistanceService = persistanceService;
        if(caller instanceof OnCreateTaskEnd)this.onCreateTaskEndHandler = (OnCreateTaskEnd)caller;
        if(caller instanceof OnUpdateTaskEnd)this.onUpdateTaskEndHandler = (OnUpdateTaskEnd)caller;
        if(caller instanceof OnDeleteTaskEnd)this.onDeleteTaskEndHandler = (OnDeleteTaskEnd)caller;
        if(caller instanceof OnGetAllTaskEnd)this.onGetAllTaskEndHandler = (OnGetAllTaskEnd)caller;
        if(caller instanceof OnSaveTaskEnd)this.onSaveTaskEndHandler = (OnSaveTaskEnd)caller;
    }

    public void getAll(){
        execute(new CrudData(OPERATION_TYPE.GET_ALL));
    }

    public void create(T entity){
        execute(new CrudData(OPERATION_TYPE.CREATE,entity));
    }

    public void update(T entity){
        execute(new CrudData(OPERATION_TYPE.UPDATE,entity));
    }

    public void save(T entity){
        execute(new CrudData(OPERATION_TYPE.SAVE,entity));
    }

    public void delete(T entity){
        execute(new CrudData(OPERATION_TYPE.DELETE,entity));
    }

    public void delete(List<T> entityList){
        execute(new CrudData(OPERATION_TYPE.DELETE_LIST,entityList));
    }

    @Override
    protected void onPostExecute(PersistenceServiceAsyncTask.CrudData crudData) {
        super.onPostExecute(crudData);
        if(crudData == null) throw new RuntimeException("crudData param is required");
        switch (crudData.operationType){
            case GET_ALL:
                if(onGetAllTaskEndHandler != null)onGetAllTaskEndHandler.onGetAllTaskEnd(crudData.result);
                break;
            case CREATE:
                if(onCreateTaskEndHandler != null)onCreateTaskEndHandler.onCreateTaskEnd();
                break;
            case UPDATE:
                if(onUpdateTaskEndHandler != null)onUpdateTaskEndHandler.onUpdateTaskEnd();
                break;
            case SAVE:
                if(onSaveTaskEndHandler != null)onSaveTaskEndHandler.onSaveTaskEnd();
                break;
            case DELETE:
            case DELETE_LIST:
                if(onDeleteTaskEndHandler != null)onDeleteTaskEndHandler.onDeleteTaskEnd(crudData.counted);
                break;
        }
    }

    @Override
    protected CrudData doInBackground(PersistenceServiceAsyncTask.CrudData ...crudDataParams) {
        if(crudDataParams == null) throw new RuntimeException("crudData param is required");
        CrudData crudData = crudDataParams[0];
        switch (crudData.operationType){
            case GET_ALL:
                crudData.result = persistanceService.getAll();
                break;
            case CREATE:
                persistanceService.create(crudData.inData);
                break;
            case UPDATE:
                persistanceService.update(crudData.inData);
                break;
            case SAVE:
                persistanceService.save(crudData.inData);
                break;
            case DELETE:
                persistanceService.delete(crudData.inData);
                break;
            case DELETE_LIST:
                for (T entity : crudData.inDataAsList) {
                    persistanceService.delete(entity);
                    crudData.counted++;
                }
                break;
        }
        return crudData;
    }

    protected class CrudData{

        public OPERATION_TYPE operationType;
        public T inData;
        public List<T> inDataAsList=new ArrayList<>();
        public List<T> result = new ArrayList<>();
        public int counted = 0;

        public CrudData(OPERATION_TYPE operationType, T inData) {
            this.operationType = operationType;
            this.inData = inData;
        }

        public CrudData(OPERATION_TYPE operationType, List<T> inDataAsList) {
            this.operationType = operationType;
            this.inDataAsList = inDataAsList;
        }

        public CrudData(OPERATION_TYPE operationType) {
            this.operationType = operationType;
        }
    }
}
