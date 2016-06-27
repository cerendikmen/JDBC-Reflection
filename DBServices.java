
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

//***************************************************************************************************************************************************

// import ...

//***************************************************************************************************************************************************




//***************************************************************************************************************************************************

public class DBServices
{
  //=================================================================================================================================================

  public  String     driver     ;
  public  String     url        ;
  public  String     database   ;
  public  String     username   ;
  public  String     password   ;

  //-------------------------------------------------------------------------------------------------------------------------------------------------

  private Connection connection ;
  private Statement  statement  ;

  //=================================================================================================================================================

  public DBServices () throws Exception
  {
    driver   = "com.mysql.jdbc.Driver"        ;
    url      = "jdbc:mysql://localhost:3306/" ;
    database = "CENG443"                      ;
    username = "root"                         ;
    password = "root"                         ;  //SİFREYİ ROOT YAP!

    Class.forName( driver ) ;

    connection = DriverManager.getConnection( url + database , username , password ) ;
    statement  = connection.createStatement()                                        ;
  }

  //=================================================================================================================================================

  synchronized List< Pair< String , Integer > > getInventory () throws Exception
  {
    ResultSet rs = statement.executeQuery("SELECT * FROM inventory");
    List< Pair< String , Integer > > list = new ArrayList<>();
    while(rs.next())
    {
        String part_name;
        Integer part_count;
        part_name = rs.getString("Part");
        part_count = rs.getInt("Count");
        Pair< String , Integer > temp = new Pair(part_name, part_count );
        list.add(temp);
        
        
    }
    return list;
  }

  //=================================================================================================================================================

  synchronized void setPartCount ( String partName , int partCount ) throws Exception
  {
    ResultSet rs = statement.executeQuery("SELECT Part FROM inventory");
    boolean isHere = false;
    while(rs.next())
    {
        if(rs.getString("Part").equals(partName))
        {
            isHere = true;
            break;
        }
    }
    if(isHere)
    {
        int iReturnValue = statement.executeUpdate("UPDATE inventory SET Count = " + partCount + " WHERE Part = '" + partName + "'");
    }
    else
    {
        int result = statement.executeUpdate("INSERT INTO inventory (Part,Count) VALUES('" + partName +"', 0)");
    }
    
  }

  //=================================================================================================================================================

  synchronized void incrementPartCount ( String partName ) throws Exception
  {
    ResultSet new_rs = statement.executeQuery("SELECT Part FROM inventory");
    boolean isHere = false;
    while(new_rs.next())
    {
        if(new_rs.getString("Part").equals(partName))
        {
            isHere = true;
            break;
        }
    }
    if(isHere)
    {
      ResultSet rs = statement.executeQuery("SELECT Count FROM inventory WHERE Part = '" + partName + "'");
      rs.next();
      int partCount = rs.getInt("Count");
      partCount++;
      int iReturnValue = statement.executeUpdate("UPDATE inventory SET Count = " + partCount + " WHERE Part = '" + partName + "'");
    }
    else
    {
       int result = statement.executeUpdate("INSERT INTO inventory (Part,Count) VALUES('" + partName +"', 0)"); 
    }
      
  }

  //=================================================================================================================================================

  synchronized void decrementPartCount ( String partName ) throws Exception
  {
    ResultSet new_rs = statement.executeQuery("SELECT Part FROM inventory");
    boolean isHere = false;
    while(new_rs.next())
    {
        if(new_rs.getString("Part").equals(partName))
        {
            isHere = true;
            break;
        }
    }
    if(isHere)
    {
      ResultSet rs = statement.executeQuery("SELECT Count FROM inventory WHERE Part = '" + partName + "'");
      rs.next();
      int partCount = rs.getInt("Count");
      partCount--;
      int iReturnValue = statement.executeUpdate("UPDATE inventory SET Count = " + partCount + " WHERE Part = '" + partName + "'");
    }
    else
    {
      int result = statement.executeUpdate("INSERT INTO inventory (Part,Count) VALUES('" + partName +"', 0)"); 
    }
      
      
      
  }

  //=================================================================================================================================================

  synchronized boolean isAvailable( String partName) throws Exception
  {
      
    ResultSet new_rs = statement.executeQuery("SELECT Part FROM inventory");
    boolean isHere = false;
    while(new_rs.next())
    {
        if(new_rs.getString("Part").equals(partName))
        {
            isHere = true;
            break;
        }
    }
    if(isHere)
    {
      ResultSet rs = statement.executeQuery("SELECT Count FROM inventory WHERE Part = '" + partName + "'");
      rs.next();
      int count = rs.getInt("Count");
      if(count > 0)
          return true;
      else
          return false;
    }
    else
    {
       int result = statement.executeUpdate("INSERT INTO inventory (Part,Count) VALUES('" + partName +"', 0)");
       return false;
    }
      
     
  }
  public void close () throws Exception
  {
    if ( statement  != null )  { statement .close() ; }
    if ( connection != null )  { connection.close() ; }
  }

  
  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

