package com.example.android.preciousplastic.db;

import android.util.Log;

import com.example.android.preciousplastic.imgur.ImgurAccessResponse;
import com.example.android.preciousplastic.imgur.ImgurAsyncGenericTask;
import com.example.android.preciousplastic.imgur.ImgurBazarItem;
import com.example.android.preciousplastic.imgur.ImgurRequestsGenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Workspace implements ImgurAccessResponse<Boolean> {

    private static final String TAG = "WORKSPACE";

    private boolean shredderMachine;
    private boolean injectionMachine;
    private boolean extrusionMachine;
    private boolean compressionMachine;
    private HashMap<String, ImgurBazarItem> itemsOnSale;

    /**
     * Default empty constructor for firebase.
     */
    public Workspace() {
        itemsOnSale = new HashMap<>();
    }

    /**
     * Constructor for a new workspace.
     *
     * @param shredderMachine    indicator if the workspace contains a shredder machine.
     * @param injectionMachine   indicator if the workspace contains an injection machine.
     * @param extrusionMachine   indicator if the workspace contains an extrusion machine.
     * @param compressionMachine indicator if the workspace contains a compression machine.
     */
    public Workspace(boolean shredderMachine, boolean injectionMachine, boolean extrusionMachine, boolean compressionMachine) {

        this.itemsOnSale = new HashMap<>();

        this.shredderMachine = shredderMachine;
        this.injectionMachine = injectionMachine;
        this.extrusionMachine = extrusionMachine;
        this.compressionMachine = compressionMachine;
    }

    public boolean isShredderMachine() {
        return shredderMachine;
    }

    public void setShredderMachine(boolean shredderMachine) {
        this.shredderMachine = shredderMachine;
    }

    public boolean isInjectionMachine() {
        return injectionMachine;
    }

    public void setInjectionMachine(boolean injectionMachine) {
        this.injectionMachine = injectionMachine;
    }

    public boolean isExtrusionMachine() {
        return extrusionMachine;
    }

    public void setExtrusionMachine(boolean extrusionMachine) {
        this.extrusionMachine = extrusionMachine;
    }

    public boolean isCompressionMachine() {
        return compressionMachine;
    }

    public void setCompressionMachine(boolean compressionMachine) {
        this.compressionMachine = compressionMachine;
    }

    public HashMap<String, ImgurBazarItem> getItemsOnSale() {
        return itemsOnSale;
    }

    public void setItemsOnSale(HashMap<String, ImgurBazarItem> itemsOnSale) {
        this.itemsOnSale = itemsOnSale;
    }

    /**
     * Updates an item's state on the bazar.
     * @param bazarItem item we wish to modify.
     * @param operation what type of action we wish to take.
     */
    public void updateBazarItem(ImgurBazarItem bazarItem, BazarOperations operation) {

        switch (operation) {

            case ADD_ITEM:
                addItem(bazarItem);
            case EDIT_ITEM:
                break;
            case REMOVE_ITEM:
                removeItemFromImgur(bazarItem);
                removeItem(bazarItem);
                break;
            case REMOVE_ALL:
                removeAllFromBazar();
        }
    }

    private void removeAllFromBazar() {
        for(Iterator<Map.Entry<String, ImgurBazarItem>> it = itemsOnSale.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ImgurBazarItem> entry = it.next();
            removeItemFromImgur(entry.getValue());
            it.remove();
        }

    }

    /**
     * Addes an item to the bazar.
     * @param bazarItem item we wish to sell.
     */
    private void addItem(ImgurBazarItem bazarItem) {
        itemsOnSale.put(String.valueOf(bazarItem.getDatetime()), bazarItem);
    }

    /**
     * Removes an item from the bazar.
     * @param bazarItem item we wish to remove.
     */
    private void removeItem(ImgurBazarItem bazarItem) {
        itemsOnSale.remove(String.valueOf(bazarItem.getDatetime()));
    }

    /**
     * Removes an item from imgur.
     * @param bazarItem item to remove.
     */
    private void removeItemFromImgur(ImgurBazarItem bazarItem) {
        final ImgurAsyncGenericTask imgurAsyncGenericTask = ImgurRequestsGenerator.generateDEL(this, bazarItem);
        imgurAsyncGenericTask.execute();
    }

    @Override
    public void getResult(Boolean asyncResult) {
        if (asyncResult != null) {
            Log.d(TAG, "getResult: item successfully removed from imgur.");
        }
    }
}
