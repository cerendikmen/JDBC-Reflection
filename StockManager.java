
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************

public class StockManager extends Employee
{
  //=================================================================================================================================================

  private int numberOfPartsSupplied ;

  //=================================================================================================================================================

  public StockManager ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;

    title                 = "Stock Manager " ;
    numberOfPartsSupplied = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    while(jobShop.isOpen)
    {
        talk("%s       %s : Checking for a reported missing part", title, name);
        String missingPart;
        synchronized(jobShop.missingParts)
        {
            while(jobShop.missingParts.isEmpty())
            {
                try{
                    talk("%s       %s : There are no reported missing parts, so I'm waiting",title, name);
                    jobShop.missingParts.wait();
                    if(!(jobShop.isOpen))
                        break;
                }
                catch(InterruptedException e){}
            }
            if(!(jobShop.isOpen))
                break;
            missingPart = jobShop.getNextMissingPart();
        }
        talk("%s       %s : Ordering part %s", title, name, missingPart);
        spendTime(200, 300);
        numberOfPartsSupplied++;
        try {
            jobShop.db.incrementPartCount(missingPart);
        } catch (Exception ex) {}
        talk("%s       %s : Part %s supplied", title, name, missingPart);
    }
    talk("%s       %s : Restocked a total of %d parts",title, name, numberOfPartsSupplied);
  }
  

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

