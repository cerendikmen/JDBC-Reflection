
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************

public class CustomerRepresentative extends Employee
{
  //=================================================================================================================================================

  private int numberOfProductRequests ;

  //=================================================================================================================================================

  public CustomerRepresentative ( String name , JobShop jobShop )
  {
    super( name , jobShop ) ;

    title                   = "Representative" ;
    numberOfProductRequests = 0                ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  private String identifyPartName ( Part part )
  {
      
    /************** PART A **************************************/ 
   
    Field nameField;
    try {
        nameField = part.getClass().getDeclaredField("name");
        nameField.setAccessible(true);
        if(nameField.getType().equals(String.class))
          try {
              return (String) nameField.get(part);
        } catch (IllegalArgumentException ex) {
        } catch (IllegalAccessException ex) {
        }
    } catch (NoSuchFieldException ex) {
        
    } catch (SecurityException ex) {
        
    }
   
    
    /****************** PART B & D *****************************************/
    Field method;
    try {
        method = part.getClass().getDeclaredField("method");
        method.setAccessible(true);
        String methodName = (String) method.get(part);
        if(methodName.equals("getName")){
          Method temp = part.getClass().getDeclaredMethod(methodName);
          temp.setAccessible(true);
          return (String) temp.invoke(part, new Object[]{});
        }
        else if (methodName.equals("getIndex")){
          Method[] methods = part.getClass().getDeclaredMethods();
          Class[] parms = null;
          Object[] args;
          for(int i = 0; i < methods.length; i++)
          {
              if(( methods[i].getName().equals(methodName)) && (methods[i].getReturnType().getTypeName().equals("int")))// RETURN TYPE INI DA KONTROL ET!!!!
              {
                  parms = methods[i].getParameterTypes();
                  break;
              }
          }
          args = new Object[parms.length];
          for(int i = 0; i < parms.length; i++)
          {
              if(parms[i].isPrimitive())
              {
                  //ASSIGN ET!!!!!
                  byte b=0;
                  short s = 0;
                  int in = 0;
                  long l = 0;
                  float f = 0;
                  double d = 0;
                  boolean bo = false;
                  char c = 'c';
                  if(parms[i].getTypeName().equals("byte"))
                  {
                      args[i] = b; 
                  }
                  else if(parms[i].getTypeName().equals("short"))
                  {
                       args[i] = s;
                  }
                  else if(parms[i].getTypeName().equals("int"))
                  {
                      args[i] = in;
                  }
                  else if(parms[i].getTypeName().equals("long"))
                  {
                      args[i] = l;
                  }
                  else if(parms[i].getTypeName().equals("float"))
                  {
                      args[i] = f;
                  }
                  else if(parms[i].getTypeName().equals("double"))
                  {
                      args[i] = d;
                  }
                  else if(parms[i].getTypeName().equals("boolean"))
                  {
                      args[i] = bo;
                  }
                  else if(parms[i].getTypeName().equals("char"))
                  {
                      args[i] = c;
                  }
              }
              else
              {
                args[i] = parms[i].newInstance();
              }
              
          }
          Method tmp = part.getClass().getDeclaredMethod(methodName, parms);
          tmp.setAccessible(true);
          int i = (int) tmp.invoke(part, args);
          char c = (char)(i+64);
          return ""+c;
        }
    } catch (NoSuchFieldException ex) {
       
    } catch (SecurityException ex) {
        
    } catch (IllegalArgumentException ex) {
          
      } catch (IllegalAccessException ex) {
          
      } catch (NoSuchMethodException ex) {
          
      } catch (InvocationTargetException ex) {
          
      } catch (InstantiationException ex) {
          
      }
    
    /***************************** PART C *************************************/
    
    Field index;
    try {
        index = part.getClass().getDeclaredField("index");
        index.setAccessible(true);
        int i = (int) index.get(part);
        char c = (char)(i+64);
        return ""+c;
    } catch (NoSuchFieldException ex) {
        
    } catch (SecurityException ex) {
        
    } catch (IllegalArgumentException ex) {
          
      } catch (IllegalAccessException ex) {
          
      }
    
    
  return null;    
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
    while(jobShop.isOpen)
    {
        talk("%s       %s : Checking for a standing product request", title, name);
        Part[] request;
        synchronized(jobShop.productRequests)
        {
            while( jobShop.productRequests.isEmpty())
            {
                try{
                   talk("%s       %s : There are no product requests, so I'm waiting.", title,name);
                   jobShop.productRequests.wait();
                   if(!(jobShop.isOpen))
                        break;
                }
                catch(InterruptedException e){}

            }
            if(!(jobShop.isOpen))
                break;
            request = jobShop.getNextProductRequest();
            numberOfProductRequests++;   
        }
        String[] partNames = new String[request.length];
        String partName;
        for(int i = 0; i < request.length; i++)
        {
            partName = identifyPartName(request[i]);
            partNames[i] = partName;    
        }
        Order order = new Order(jobShop.generateNewOrderID(),partNames);
        synchronized(jobShop.workingOrders)
        {
            jobShop.addWorkingOrder(order);
            jobShop.workingOrders.notifyAll();
        }
        talk("%s       %s : I am adding a new order %s", title, name, order.toString());
        spendTime(500, 600);
        
    }
    talk("%s       %s : Processed a total of %d product requests", title, name, numberOfProductRequests); 
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

