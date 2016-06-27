
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************

public class Worker extends Employee
{
  //=================================================================================================================================================

  private int numberOfPartsAssembled ;

  //=================================================================================================================================================

  public Worker ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;

    title                  = "Worker        " ;
    numberOfPartsAssembled = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
      while(jobShop.isOpen)
      {
        talk("%s       %s : Checking for a working order", title, name);
        Order order;
        synchronized(jobShop.workingOrders)
        {
            while(jobShop.workingOrders.isEmpty())
            {
                try{
                    talk("%s       %s : There are no working orders, so I'm waiting",title, name);
                    jobShop.workingOrders.wait();
                    if(!(jobShop.isOpen))
                        break;
                }
                catch(InterruptedException e){}
            }
            if(!(jobShop.isOpen))
                break;
            order = jobShop.getNextWorkingOrder();
        }
        talk("%s       %s : Currently working on order %s",title, name, order.toString());
        if(!(order.isCompleted()))
        {
            
            String part = order.nextRemainingPart();
            
            try {
                
                    if(!(jobShop.db.isAvailable(part)))
                    {
                        synchronized(jobShop.missingParts)
                        {
                            jobShop.addMissingPart(part);
                            jobShop.missingParts.notifyAll();
                            jobShop.addWorkingOrder(order);
                        }
                    }
                    else
                    {
                        jobShop.db.decrementPartCount(part);
                        spendTime(200, 300);
                        order.completeNextRemainingPart();
                        numberOfPartsAssembled++; 
                        
                        if(order.isCompleted())
                        {
                            jobShop.addCompletedOrder(order);
                            talk("%s       %s : Assembled last part of order %s", title, name, order.toString());
                        }
                        else
                        {
                            jobShop.addWorkingOrder(order);
                            talk("%s       %s : Assembled next part of order %s", title, name, order.toString());
                        }
                        jobShop.addCompletedOrder(order);
                        
                    }      
            } catch (Exception ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
             
        }
      }
      talk("%s       %s : Assembled a total of %d parts", title, name, numberOfPartsAssembled);
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

