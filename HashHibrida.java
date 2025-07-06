package Models;
import java.util.*;

public class HashHibrida {

    private static class Entrada {
        LinkedList<Transaction> porId = new LinkedList<>();
        Object origemEstrutura = null; // Pode ser Transaction, TreeAVL ou TreeRB futuramente
        int colisoesOrigem = 0;
    }

    private int M = 512;
    private Entrada[] tabela;

    public HashHibrida() {
        tabela = new Entrada[M];
        for (int i = 0; i < M; i++) tabela[i] = new Entrada();
    }

    private int hashId(String id) {
        return (id.hashCode() & 0x7fffffff) % M;
    }

    private int hashOrigem(String origem, int tentativa) {
        int h = (origem.hashCode() & 0x7fffffff) % M;
        return (h + tentativa * tentativa) % M;
    }

    public List<Transaction> buscarPorId(String id) {
        int idxId = hashId(id);
        List<Transaction> resultado = new ArrayList<>();
        for (Transaction t : tabela[idxId].porId) {
            if (t.getId().equals(id)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public void inserir(Transaction t) {
        // 1. Inserir pelo ID (encadeamento)
        int idxId = hashId(t.getId());
        tabela[idxId].porId.add(t);

        // 2. Inserir pela ORIGEM (sondagem quadrática)
        int tentativas = 0;
        int idxOrigem;
        while (true) {
            idxOrigem = hashOrigem(t.getOrigem(), tentativas);
            Entrada entrada = tabela[idxOrigem];

            if (entrada.origemEstrutura == null) {
                entrada.origemEstrutura = t;
                entrada.colisoesOrigem = 0;
                break;
            } else if (entrada.origemEstrutura instanceof Transaction otherT && otherT.getOrigem().equals(t.getOrigem())) {
                // Colisão com mesma origem
                entrada = tabela[idxOrigem];
                List<Transaction> lista = new ArrayList<>();
                lista.add((Transaction) entrada.origemEstrutura);
                lista.add(t);
                entrada.origemEstrutura = lista;
                entrada.colisoesOrigem++;
                break;
            } else if (entrada.origemEstrutura instanceof List<?> tempList &&
                    ((Transaction) tempList.get(0)).getOrigem().equals(t.getOrigem())) {

                @SuppressWarnings("unchecked")
                List<Transaction> lista = (List<Transaction>) tempList;

                lista.add(t);
                entrada.colisoesOrigem++;
                break;
            } else {
                tentativas++;
                if (entrada.colisoesOrigem > 3) {
                    List<Transaction> listaTransacoes;

                    if (entrada.origemEstrutura instanceof Transaction tx) {
                        listaTransacoes = new ArrayList<>();
                        listaTransacoes.add(tx);
                    } else if (entrada.origemEstrutura instanceof List<?>) {
                        listaTransacoes = (List<Transaction>) entrada.origemEstrutura;
                    } else {
                        // Já está migrado, só insere na árvore
                        if (entrada.origemEstrutura instanceof AVLTree) {
                            AVLTree<Transaction> arvoreAVL = (AVLTree<Transaction>) entrada.origemEstrutura;
                            arvoreAVL.insert(t);
                        } else if (entrada.origemEstrutura instanceof RBTree) {
                            RBTree<Transaction> arvoreRB = (RBTree<Transaction>) entrada.origemEstrutura;
                            arvoreRB.insert(t);
                        }
                        break;
                    }

                    // Cria árvore AVL e insere os elementos
                    AVLTree<Transaction> arvoreAVL = new AVLTree<>();
                    for (Transaction tr : listaTransacoes) {
                        arvoreAVL.insert(tr);
                    }

                    // Insere novo elemento
                    arvoreAVL.insert(t);

                    // Substitui na tabela
                    entrada.origemEstrutura = arvoreAVL;

                    // Zera colisões (opcional)
                    entrada.colisoesOrigem = 0;

                    break;
                }
            }
        }
    }

    public void printTabela() {
        for (int i = 0; i < M; i++) {
            Entrada e = tabela[i];
            if (!e.porId.isEmpty() || e.origemEstrutura != null) {
                System.out.print("[" + i + "] ID: ");
                for (Transaction t : e.porId) System.out.print(t + " ");
                System.out.print("| ORIGEM: ");
                if (e.origemEstrutura instanceof Transaction t) System.out.print(t);
                else if (e.origemEstrutura instanceof List<?> list) System.out.print(list);
                else if (e.origemEstrutura != null) System.out.print("[Estrutura complexa]");
                System.out.println();
            }
        }
    }
}


