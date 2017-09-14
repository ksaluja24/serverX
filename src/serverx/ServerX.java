/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverx;

/**
 *
 * @author Kp Saluja
 */
import Server.interfaces.ServerInterface;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class ServerX extends UnicastRemoteObject implements Server.interfaces.ServerInterface {
static ServerInterface x;
public static String password;
static int check=0;
public static int myid;
public static int registerId;
 static Registry reg;
    /**
     * @param args the command line arguments
     */
 static {
    try {
        reg = LocateRegistry.createRegistry(5040);
    } catch (RemoteException ex) {
        System.out.println(ex);
    }
 }
    private String name;
    private String obtainedPassword;
ServerX() throws RemoteException
   {
     

   }

public static Connection getConnection() 
{
Connection connection=null;
try
{
Class.forName("com.mysql.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/scratchdb","root","kamal");
}catch(Exception exception)
{
System.out.println(exception);
}
return connection;
}

    public boolean existsByEmail(String email) throws RemoteException
    {
    try
        {
            
            Connection connection=ServerX.getConnection();          
            PreparedStatement ps=connection.prepareStatement("select * from logindata where email=?");
            ps.setString(1,email);
            
            ResultSet rs=ps.executeQuery();
            
             if(rs.next())
             {
              myid= rs.getInt("scratch_id");
               password=rs.getString("password") ;   
               rs.close();
             ps.close();
             connection.close();    
             return true;
            
             }
               
        }catch(Exception e)
        {
        System.out.println(e);
        }
    return false;
    
    }
    public int getID() throws RemoteException
    {
     System.out.println("Get id returns "+myid);
    return myid;
    }
    public String getPassword() throws RemoteException
    {
    return password;
    }
public String getNameAndPassword(int id) throws RemoteException
{
try
        {
    
            Connection connection=ServerX.getConnection();          
            PreparedStatement ps=connection.prepareStatement("select * from user_data where scratch_id=?");
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();
          if(rs.next())
          {
         name=rs.getString("firstname");
            rs.close();
            ps.close();
          }
            ps=connection.prepareStatement("select * from logindata where scratch_id=?");
                    ps.setInt(1,id);
                    rs=ps.executeQuery();
                    rs.next()   ;
                    obtainedPassword=rs.getString("password");
                    rs.close();
                    ps.close();
                    connection.close();
        }catch(Exception e)
        {
        System.out.println(e);
        }
return name+","+obtainedPassword;
}
   public boolean registerToDatabase(String email,String password,int myid,String firstname,String lastname) throws RemoteException
   {
   try
{
    
    Connection connection=ServerX.getConnection();
    PreparedStatement ps=connection.prepareStatement("insert into logindata(email,password) values(?,?)");
    ps.setString(1, email);
    ps.setString(2,password);
    ps.executeUpdate();
   ResultSet rs =ps.getGeneratedKeys();
   rs.next();
   registerId=rs.getInt(1);
    rs.close();
    ps.close();
  ps=connection.prepareStatement("insert into user_data values(?,?,?)");
    ps.setInt(1,registerId);
    ps.setString(2,firstname);
    ps.setString(3,lastname);
    ps.executeUpdate();
    ps.close();
    connection.close();
    
 return true;
            }catch(Exception e)
{
System.out.println(e);
return false;
}
   
   }
    public int getRegisterId()throws RemoteException
    {
     return registerId;   
    }
public static void start() 
{
     if(check==0)
     {
    try
{
         x = new ServerX();
 reg.rebind("LionKing",x);
 System.out.println("Server LionKing Started");
check=1;
}catch(Exception e)
{
System.out.println(e);
}
     }
     else
     {
     System.out.println("Server is Already Running..");
     }
}
public static void stop()
{
    if(check==1)
    {
    try{
        // Unregister ourself
        reg.unbind("LionKing");

        // Unexport; this will also remove us from the RMI runtime
        UnicastRemoteObject.unexportObject(x,true);

        System.out.println("Server Stopped");
        check=0;
    }
    catch(Exception e){
        System.out.println(e);
    }
    }
    else
    {
    System.out.println("Server is not Running....");
    }
    }
}
