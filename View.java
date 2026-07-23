import pt.iscte.guitoo.Color;
import pt.iscte.guitoo.StandardColor;
import pt.iscte.guitoo.board.Board;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;


public class View {
    private Board board;
    private Damas jogo;
    
    

    public View(int boardSize, int pieceNumber) {
        jogo = new Damas(boardSize, pieceNumber);
        board = new Board("Damas", boardSize, boardSize, 60);

        board.setIconProvider(this::icon);
        board.setBackgroundProvider(this::background);
        board.setTitle(jogo.wTurn == true ? "brancas jogam" : "pretas jogam");
        board.addMouseListener(this::makeClicks);
        
        board.addAction("New game", this::action);
        board.addAction("Random", this::action1);
        board.addAction("Salvar", this::action2);
        board.addAction("Carregar", this::action3);
        
    }

    private String icon(int row, int col) {
    	char piece = jogo.getTheIcon(row, col);
        return piece == 'W' ? "white.png" : (piece == 'B' ? "black.png" : null);
    }

    public Color background(int row, int col) {
    	if (jogo.selectedPiece != null && row == jogo.selectedPiece[0] && col== jogo.selectedPiece[1]) {
    		return StandardColor.YELLOW;
    	}
    	return (row + col) % 2 == 0 ? StandardColor.WHITE : StandardColor.BLACK;
    }

    public void start() {
        board.open();
    }

    private void makeClicks(int row, int col) {
    	if (!jogo.isValidPosition(row, col)) {
			return;
			
		}
    	//primeiro click
		if (jogo.selectedPiece == null) {
            if (jogo.selectedIsValid(row, col)) {
                jogo.selectedPiece = new int[] { row, col };
            }
            return;
        }
		
		boolean canCapture= jogo.needToCapture();
		// segundo click - (mover ou remover o selected)
		if(canCapture) {
			if (jogo.isValidMove(jogo.selectedPiece[0], jogo.selectedPiece[1], row, col) && Math.abs(row - jogo.selectedPiece[0]) == 2) {
				jogo.movePiece(jogo.selectedPiece[0], jogo.selectedPiece[1], row, col);
	                jogo.wTurn = !jogo.wTurn;
	                
			}else {
				board.showMessage("Tens de capturar");
				jogo.selectedPiece= null;
			}
		}else {
			if (jogo.isValidMove(jogo.selectedPiece[0], jogo.selectedPiece[1], row, col)) {
				jogo.movePiece(jogo.selectedPiece[0], jogo.selectedPiece[1], row, col);
				jogo.wTurn = !jogo.wTurn;
			}
		}
		
		jogo.selectedPiece = null;
        board.setTitle(jogo.wTurn == true ? "brancas jogam" : "pretas jogam");
        board.refresh();
        
        if (jogo.gameOver()) {
            String result = jogo.winDraw();
            if (result.equals("Empate")) {
                board.showMessage("Empate");
            } else {
                board.showMessage("As " + result + " ganharam!");
            }
        }
    

        
    }
    public void action() {
    	int boardSize = board.promptInt("Tamanho fo tabuleiro: entre 4-16)?");
        if (boardSize >= 4 && boardSize <= 16) {
            int numberOfPieces = board.promptInt("Número de peças por lado (1-" + (boardSize * boardSize / 4 - boardSize + (boardSize/2)) + ")?");
            if (numberOfPieces >= 1 && numberOfPieces <= boardSize * boardSize / 4 - boardSize + boardSize/2) {
                View newGui = new View(boardSize, numberOfPieces);
                newGui.start();
            } else  {
                board.showMessage("Número de peças inválido!");
            }
        } else {
            board.showMessage("Tamanho do tabulero inválido!");
        }
    	
    } 
    public void action1() {
    	int[] randomMove = null;
        if (jogo.needToCapture()) {
            randomMove = jogo.randomCapMove();
        } else {
            randomMove = jogo.randomMove();
        }

        if (randomMove != null) {
            makeClicks(randomMove[0], randomMove[1]);
            makeClicks(randomMove[2], randomMove[3]);
            board.refresh();
            board.setTitle(jogo.wTurn == true ? "brancas jogam" : "pretas jogam");
        }
    }
    	
    
    public void action2() {
    	try {
            String filename = board.promptText("Nome do jogo que quer salvar");
            if (filename == null) {
                board.showMessage("Ação cancelada");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println(jogo.SIZE);
                writer.println(jogo.wTurn);
                
                for (int i = 0; i < jogo.SIZE; i++) {
                    for (int j = 0; j < jogo.SIZE; j++) {
                        writer.print(jogo.getTheIcon(i, j));
                    }
                    writer.println();
                }
                
                board.showMessage("Jogo salvo com sucesso");
            }
        } catch (Exception e) {
            board.showMessage("Erro ao salvar o jogo");
        }
    }
    public void action3() {
    	try {
    		String filename = board.promptText("Nome do ficheiro");
            if (filename == null || filename.trim().isEmpty()) {
                board.showMessage("Load cancelado.");
            }
            try (Scanner scanner= new Scanner(new File(filename))) {
            	int loadSize=scanner.nextInt();
            	jogo = new Damas(loadSize, (loadSize*loadSize) /4  - loadSize + (loadSize/2));
            	jogo.wTurn = scanner.nextBoolean();
            	scanner.nextLine();
            	for (int i=0; i<loadSize; i++) {
            		String line= scanner.nextLine();
            		for (int j=0; j<loadSize; j++) {
            			jogo.board[i][j]= line.charAt(j);
            		}
            	}
            	board.setTitle(jogo.wTurn == true ? "brancas jogam" : "pretas jogam");
            	board.refresh();
            }
    	}catch(Exception e){
    		board.showMessage("Erro");
    	}
    	
    }

    public static void main(String[] args) {
        View gui = new View(8,12);
        gui.start();
    }
}