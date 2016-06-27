//***************************************************************************************************************************************************

import java.util.Random ;
import java.util.logging.Level;
import java.util.logging.Logger;

//***************************************************************************************************************************************************




//***************************************************************************************************************************************************

public class Customer extends Thread implements Person
{
  //=================================================================================================================================================

  public        String  title                   ;
  public        String  name                    ;
  public        JobShop jobShop                 ;

  //-------------------------------------------------------------------------------------------------------------------------------------------------

  private       int     numberOfProductRequests ;
  private final Random  random                  ;

  //=================================================================================================================================================

  public Customer ( String name , JobShop jobShop )
  {
    this.title                   = "Customer      " ;
    this.name                    = name             ;
    this.jobShop                 = jobShop          ;
    this.numberOfProductRequests = 0                ;
    this.random                  = new Random()     ;

    talk( "%s %s : (Constructor finished)" , title , name ) ;
  }

  //=================================================================================================================================================

  @Override
  public void talk ( String format , Object ... args )  // This is a synchronized wrapper for printf method
  {
    synchronized ( System.out )  { System.out.printf( format + "%n" , args ) ;  System.out.flush() ; }
  }

  //=================================================================================================================================================

  @Override
  public void spendTime ( int minMilliseconds , int maxMilliseconds )  // This is a wrapper for Thread.sleep
  {
    int duration = minMilliseconds + (int) ( Math.random() * ( maxMilliseconds - minMilliseconds ) ) ;

    try { Thread.sleep( duration ) ; } catch ( InterruptedException ex ) { /* Do nothing */ }
  }

  //=================================================================================================================================================

  private Part generateRandomPart ()
  {
    int randomNumber = random.nextInt(4);
    if(randomNumber == 0)
        return new PartA();
    else if(randomNumber == 1)
        return new PartB();
    else if(randomNumber == 2)
        return new PartC();
    else if(randomNumber == 3)
        return new PartD();
    return null;
    
  }

  //=================================================================================================================================================

  private Part [] generateRandomProductRequest ()
  {
    int     numberOfParts = 2 + random.nextInt( 4 )   ;
    Part [] parts         = new Part[ numberOfParts ] ;

    for ( int i = 0 ; i < numberOfParts ; i++ )  { parts[ i ] = generateRandomPart() ; }

    return parts ;
  }

  //=================================================================================================================================================

  @Override
  public void run ()
  {
      while(jobShop.isOpen) {
          Part[] request = generateRandomProductRequest();
          synchronized(jobShop.productRequests)
          {
            jobShop.addProductRequest(request);
            numberOfProductRequests++; 
            jobShop.productRequests.notifyAll();
          }
          talk("%s       %s : Submitting a product request of %d parts", title, name, request.length);
          spendTime(800, 1000);
      }
      talk("%s       %s : Submitted a total of %d product requests to the jobshop", title, name, numberOfProductRequests);
      
  }

  //=================================================================================================================================================
}

//***************************************************************************************************************************************************

