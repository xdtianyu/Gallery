package org.xdty.gallery.model.db;

import android.os.Parcelable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Table;

@Table(name = "server")
@Entity
public interface IServer extends Parcelable {

    @Key
    @Generated
    int getId();

    String getUri();

    String getUsername();

    String getPassword();

}
