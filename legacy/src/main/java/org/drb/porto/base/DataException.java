package org.drb.porto.base;

public class DataException extends Exception
{
   private static final long serialVersionUID = 1L;

   public DataException( String str )
   {
      super( str );
   }
   
   public DataException( String str, Throwable e )
   {
      super( str, e );
   }

}
