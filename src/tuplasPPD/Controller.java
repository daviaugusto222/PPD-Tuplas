/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tuplasPPD;
import java.rmi.RemoteException;
import net.jini.space.JavaSpace;
import net.jini.core.lease.Lease;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;
/**
 *
 * @author user
 */
public class Controller {
    
    private JavaSpace space;
    
    //Tenta encontrar o javaspace assim que inicia o controller
    public Controller() {

        try {
            System.out.println("Procurando pelo servico JavaSpace...");
            Lookup finder = new Lookup(JavaSpace.class);
            this.space =  (JavaSpace) finder.getService();
            if (space == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
                System.exit(-1);
            }
            System.out.println("O servico JavaSpace foi encontrado");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    public String criarAmbiente() {
        String finalName;
        try {
            Ambiente novoAmb = new Ambiente();
            int ambIndex = 0;

            while (true) {
                ambIndex += 1;
                String ambName = "amb" + ambIndex;
                novoAmb.nome = ambName;
                Ambiente tempAmb = (Ambiente) this.space.read(novoAmb, null, JavaSpace.NO_WAIT);

                if (tempAmb == null) {
                    this.space.write(novoAmb, null, Lease.FOREVER);
                    System.out.println("\r\nAmbiente " + ambName + " criado");
                    finalName = ambName;
                    break;
                }

            }
            return finalName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String criarDispositivo() {
        String finalName;
        try {
            Dispositivo novoDisp = new Dispositivo();
            int dispIndex = 0;

            while (true) {
                dispIndex += 1;
                String dispName = "disp" + dispIndex;
                novoDisp.nome = dispName;
                Dispositivo tempDisp = (Dispositivo) space.read(novoDisp, null, JavaSpace.NO_WAIT);

                if (tempDisp == null) {
                    space.write(novoDisp, null, Lease.FOREVER);
                    System.out.println("\r\nDispositivo " + dispName + " criado");
                    finalName = dispName;
                    break;
                }
            }
            return finalName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String criarUsuario() {
        String finalName;
        try {
            Usuario usuario = new Usuario();
            int userIndex = 0;

            while (true) {
                userIndex += 1;
                String userName = "user" + userIndex;
                usuario.nome = userName;
                Usuario tempUser = (Usuario) space.read(usuario, null, JavaSpace.NO_WAIT);

                if (tempUser == null) {
                    space.write(usuario, null, Lease.FOREVER);
                    System.out.println("\r\nUsuario registrado como: " + usuario.nome);
                    finalName = userName;
                    break;
                }
            }
            return finalName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void moverDispositivo(String nome, String novoAmb) {
        try {
            Dispositivo oldDisp = new Dispositivo();
            oldDisp.nome = nome;
            Dispositivo disp = (Dispositivo) space.take(oldDisp, null, JavaSpace.NO_WAIT);

            disp.amb = novoAmb;
            space.write(disp, null, Lease.FOREVER);
            System.out.println("\r\nDispositivo " + nome + " movido para o ambiente " + novoAmb);

        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String listarDispositivo(String ambNome) {
        String nomesDispositivos = "";
        String nomesDispositivosNull = "Não existe nenhum dispositivo no ambiente";
        try {
            List<Dispositivo> listaDisp = ManagerSpace.listaDispositivo(space, ambNome);
            for (int i = 0; i < listaDisp.size(); i++) {
                nomesDispositivos += listaDisp.get(i).nome;
                if (i < listaDisp.size() - 1) {
                    nomesDispositivos += ", ";
                }
            }
            System.out.println(nomesDispositivos);
            nomesDispositivos = "Esses são os dispositivos que estão no ambiente: " + nomesDispositivos;

            if (listaDisp.isEmpty()) {
                nomesDispositivos = nomesDispositivosNull;
            }

            listaDisp.clear();
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nomesDispositivos;
    }

    public int contadorDispositivos(String ambNome) {
        try {
            List<Dispositivo> listaDisp = ManagerSpace.listaDispositivo(space, ambNome);
            return listaDisp.size();
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public void excluirAmbiente(String oldNome) {
        try {
            Ambiente oldAmb = new Ambiente();
            oldAmb.nome = oldNome;
            space.take(oldAmb, null, JavaSpace.NO_WAIT);
            System.out.println("\r\nAmbiente " + oldNome + " destruido");

        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void excluirDispositivo(String oldNome) {
        try {
            Dispositivo oldDisp = new Dispositivo();
            oldDisp.nome = oldNome;
            space.take(oldDisp, null, JavaSpace.NO_WAIT);
            System.out.println("\r\nDispositivo " + oldNome + " destruido");

        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void moverUsuario(String nome, String novoAmb) {
        try {
            Usuario oldUser = new Usuario();
            oldUser.nome = nome;
            Usuario usuario = (Usuario) space.take(oldUser, null, JavaSpace.NO_WAIT);

            oldUser.amb = novoAmb;
            space.write(oldUser, null, Lease.FOREVER);
            System.out.println("\r\nUsuario " + nome + " movido para o ambiente " + novoAmb);

        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String listarUsuario(String ambNome) {
        String nomesUsuarios = "";
        String nomesUsuariosNull = ". Não existe nenhum usuário no ambiente";
        try {
            List<Usuario> listaUser = ManagerSpace.listaUsuario(space, ambNome);
            for (int i = 0; i < listaUser.size(); i++) {
                nomesUsuarios += listaUser.get(i).nome;
                if (i < listaUser.size() - 1) {
                    nomesUsuarios += ", ";
                }
            }
            System.out.println(nomesUsuarios);
            nomesUsuarios = ". Esses são os dispositivos que estão no ambiente: " + nomesUsuarios;

            if (listaUser.isEmpty()) {
                nomesUsuarios = nomesUsuariosNull;
            }

            listaUser.clear();
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nomesUsuarios;
    }

    public String listarUsuarioBatePapo(String nomeUsuario) {
        String nomesUsuarios = "";
        try {
            Usuario user = ManagerSpace.encontraUsuario(space, nomeUsuario);
            String ambNome = user.amb;

            List<Usuario> listaUser = ManagerSpace.listaUsuario(space, ambNome);
            for (int i = 0; i < listaUser.size(); i++) {
                nomesUsuarios += listaUser.get(i).nome;
                if (i < listaUser.size() - 1) {
                    nomesUsuarios += ", ";
                }
            }
            listaUser.clear();
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return nomesUsuarios;
    }

    public String ambUsuarioBatePapo(String nomeUsuario) {
        String ambienteNome = "";
        try {
            Usuario user = ManagerSpace.encontraUsuario(space, nomeUsuario);
            ambienteNome = user.amb;
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ambienteNome;
    }

    public void excluirUsuario(String oldNome) {
        try {
            Usuario oldUser = new Usuario();
            oldUser.nome = oldNome;
            space.take(oldUser, null, JavaSpace.NO_WAIT);
            System.out.println("\r\nUsuario " + oldUser + " destruido");

        } catch (UnusableEntryException | TransactionException | InterruptedException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void enviarMensagem(String nomeTo, String usuario, String ambAtual, String mensagemBP) {
        try {
            Mensagem novaMsg = new Mensagem();
            novaMsg.time = System.currentTimeMillis();
            novaMsg.from = usuario;
            novaMsg.to = nomeTo;
            novaMsg.amb = ambAtual;
            novaMsg.msg = mensagemBP;

            space.write(novaMsg, null, Lease.FOREVER);
            System.out.println("\r\nMensagem enviada a " + nomeTo);
        } catch (TransactionException | RemoteException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String receberMensagem(String usuario, String ambAtual) {
        String mensagem = "";
        try {
            Mensagem msgTemplate = new Mensagem();
            msgTemplate.to = usuario;
            msgTemplate.amb = ambAtual;

            Mensagem novaMsg = (Mensagem) space.take(msgTemplate, null, JavaSpace.NO_WAIT);

            if (novaMsg == null) {
                System.out.println("\r\nNao ha novas mensagens");
            } else {
                System.out.println();

                while (novaMsg != null) {
                    mensagem = novaMsg.from + ": " + novaMsg.msg + "\n";
                    novaMsg = (Mensagem) space.take(msgTemplate, null, JavaSpace.NO_WAIT);
                    
                }
            }
        } catch (RemoteException | UnusableEntryException | TransactionException | InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensagem;
    }
      
}
