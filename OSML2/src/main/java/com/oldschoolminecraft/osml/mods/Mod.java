package com.oldschoolminecraft.osml.mods;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Mod
{
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty enabled = new SimpleBooleanProperty();
    
    private String filePath;
    
    public Mod(String fileName, String filePath)
    {
        this.filePath = filePath;
        
        setName(fileName);
        setEnabled(true);
    }
    
    public Mod() {}
    
    public String getName()
    {
        return nameProperty().get();
    }
    
    public String getPath()
    {
        return filePath;
    }
    
    public String toString()
    {
        return getName();
    }
    
    public final StringProperty nameProperty()
    {
        return name;
    }
    
    public final BooleanProperty enabledProperty()
    {
        return enabled;
    }
    
    public final void setName(final String name)
    {
        this.nameProperty().set(name);
    }
    
    public final void setEnabled(final boolean enabled)
    {
        this.enabledProperty().set(enabled);
    }
}
