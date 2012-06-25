package com.github.norbo11.deathbind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MethodsFile
{
    public MethodsFile(DeathBind p)
    {
        this.p = p;
    }
    DeathBind p;
    
    public void addToFile(String filename, String lineToAdd)
    {
        try
        {
            File file = new File(p.pluginDir, "\\" + filename);
            FileWriter writer = new FileWriter(file, true);
            writer.append(lineToAdd);
            writer.flush();
            writer.close();
        } catch (Exception e)
        {
            p.log.info("An error has occured when adding to a file " + filename);
            e.printStackTrace();
        }
    }
    
    public String readFile(String filename)
    {
        try
        {
            File file = new File(p.pluginDir, "\\" + filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String contents = "";
            String temp = "";

            while ((temp = reader.readLine()) != null)
                contents = contents + temp + "\n";
            reader.close();

            return contents;
        } catch (Exception e)
        {
            p.log.info("An error has occured when reading a file " + filename);
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> splitByLine(String toSplit)
    {
        List<String> returnValue = new ArrayList<String>();
        String[] lines = toSplit.split("\n");
        for (String temp : lines)
            returnValue.add(temp);
        return returnValue;
    }
    
    public void setFile(String filename, String content)
    {
        try
        {
            File file = new File(p.pluginDir, "\\" + filename);
            FileWriter writer = new FileWriter(file, false);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e)
        {
            p.log.info("An error has occured when setting a file " + filename);
            e.printStackTrace();
        }
    }
}
