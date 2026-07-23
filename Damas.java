public class Damas {
	public int SIZE = 8;
	char[][] board;
	boolean wTurn;
	public int[] selectedPiece;
	public boolean whitePiece;
	public int direction;
	

	public Damas(int boardSize, int numberOfPieces) {
		this.SIZE = boardSize; //pode ser preciso corrigir isto
        board = new char[SIZE][SIZE]; 
		wTurn = true;
		selectedPiece = null;
		initializeBoard(numberOfPieces);
	}

	private void initializeBoard(int numberOfPieces) {
		// Por o tabuleriro em branco (sem nada)
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            board[i][j] = ' ';
	        }
	    }

	    // Colocar peças pretas
	    int placeBlack = 0;
	    for (int row = 0; row < SIZE && placeBlack < numberOfPieces; row++) {
	        for (int col = 0; col < SIZE && placeBlack < numberOfPieces; col++) {
	            if ((row + col) % 2 == 1) {
	                board[row][col] = 'B';
	                placeBlack++;
	            }
	        }
	    }

	    // Colocar peças brancas
	    int placeWhite = 0;
	    for (int row = SIZE - 1; row >= 0 && placeWhite < numberOfPieces; row--) {
	        for (int col = SIZE - 1; col >= 0 && placeWhite< numberOfPieces; col--) {
	            if ((row + col) % 2 == 1) {
	                board[row][col] = 'W';
	                placeWhite++;
	            }
	        }
	    }
	}
	public char getTheIcon(int row, int col) {
        return board[row][col];
    
	}

	
	public boolean isValidPosition(int row, int col) {
		return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
	}
	
	public boolean selectedIsValid(int row, int col) {
		if (board[row][col] == ' ') {
			return false;
		}
		whitePiece = board[row][col] == 'W';
		return (whitePiece && wTurn) || (!whitePiece && !wTurn);
	}
	boolean isValidMove(int startRow, int startCol, int endRow, int endCol) {
		int midRow = (startRow+endRow)/2;
		int midCol= (startCol + endCol)/2;
		if (board[endRow][endCol] != ' ' || (endRow + endCol) % 2 == 0) {
			return false;
		}

		whitePiece = board[startRow][startCol] == 'W';
		direction = whitePiece ? -1 : 1;

		// move normal
		if (endRow == startRow + direction && Math.abs(endCol - startCol) == 1) {
			return true;
		}

		// move de captura
		if (endRow == startRow +2* direction  && Math.abs(endRow- startRow)== 2 && ((wTurn && board[midRow][midCol]=='B') || (!wTurn && board[midRow][midCol]=='W'))) {
			if (board[midRow][midCol] == ' ') {
				return false;
			}
			return true;
        
		}
			
		return false;
	}
	public void movePiece(int startRow, int startCol, int endRow, int endCol) {
		int midRow = (startRow + endRow) /2;
		int midCol= (startCol + endCol)/2 ;
		// mover peça
		board[endRow][endCol] = board[startRow][startCol];
		board[startRow][startCol] = ' ';
		//capturar peça
		if (Math.abs(endRow - startRow) == 2) {
            board[midRow][midCol] = ' ';
		}
	}
	public boolean gameOver() {
	    int whiteP = 0;
	    int blackP = 0;
	    boolean bHasMoves = false;
	    boolean wHasMoves = false;
	    
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (board[i][j] == 'W') {
	                whiteP++;
	                if (pieceHasValidMoves(i, j)) {
	                    wHasMoves = true;
	                }
	            }
	            if (board[i][j] == 'B') {
	                blackP++;
	                if (pieceHasValidMoves(i, j)) {
	                    bHasMoves = true;
	                }
	            }
	        }
	    }
	    return (whiteP == 0 || blackP == 0 || (wTurn && !wHasMoves) || (!wTurn && !bHasMoves));
	}
	private boolean pieceHasValidMoves(int row, int col) {
		if (!isValidPosition(row, col) || board[row][col]== ' ')
			return false;
		whitePiece= board[row][col] == 'W';
		direction = whitePiece ? -1 : 1;
		
		// ver movimentos de captura
		for (int nc = -2; nc <= 2; nc += 4) {
	        int newR = row + 2 * direction;
	        int newC = col + nc;
	        if (isValidPosition(newR, newC) && isValidMove(row, col, newR, newC)) {
	            return true;
	        }
	    }

	    // ver movimentos normais
	    for (int nc = -1; nc <= 1; nc += 2) {
	        int newR = row + direction;
	        int newC = col + nc;
	        if (isValidPosition(newR, newC) && isValidMove(row, col, newR, newC)) {
	            return true;
	        }
	    }
	    return false;
	}
	public boolean hasCaptureMoves(int row, int col) {
		if (board[row][col] == ' ')
	        return false;
	    whitePiece = board[row][col] == 'W';
	    direction = whitePiece ? -1 : 1;
	    for (int nc = -2; nc<=2; nc+=4) {
	        int newR = row + 2 * direction;
	        int newC = col + nc;
	        if (isValidPosition(newR, newC) && isValidMove(row, col, newR, newC)) {
	            return true;
	        }
	    }
	    return false;
	}
	public boolean needToCapture() {
		for (int i=0; i< SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if((wTurn&& board[i][j]=='W')|| (!wTurn && board[i][j]=='B')){
					if (hasCaptureMoves(i,j)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String winDraw() {
	    int whiteP = 0;
	    int blackP = 0;
	    
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if (board[i][j] == 'W') {
	                whiteP++;
	            }
	            if (board[i][j] == 'B') {
	                blackP++;
	            }
	        }
	    }
	    
	    if (whiteP > blackP) {
	        return "Brancas";
	    } else if (blackP > whiteP) {
	        return "Pretas";
	    } else {
	        return "Empate";
	    }
	}
	
	
	public int[] randomCapMove(){
		int[][] capMoves = new int[SIZE*SIZE][4];
	    int moveCount = 0;
	    
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if ((wTurn && board[i][j] == 'W') || (!wTurn && board[i][j] == 'B')) {
	                direction = (board[i][j] == 'W') ? -1 : 1;                
	                for (int nc=-2; nc <= 2; nc += 4) {
	                    int newR = i + 2 * direction;
	                    int newC = j + nc;
	                    
	                    if (isValidPosition(newR, newC) && isValidMove(i, j, newR, newC)) {
	                        capMoves[moveCount][0] = i;
	                        capMoves[moveCount][1] = j;
	                        capMoves[moveCount][2] = newR;
	                        capMoves[moveCount][3] = newC;
	                        moveCount++;
	                    }
	                }
	            }
	        }
	    }
	    
	    return moveCount > 0 ? capMoves[(int)(Math.random() * moveCount)] : null;
	}
	public int[] randomMove() {
		int[][] validMove = new int[SIZE*SIZE][4];
	    int moveCount = 0;
	    
	    for (int i = 0; i < SIZE; i++) {
	        for (int j = 0; j < SIZE; j++) {
	            if ((wTurn && board[i][j] == 'W') || (!wTurn && board[i][j] == 'B')) {
	                direction = wTurn ? -1 : 1;
	                
	                // ver movimentos normais
	                for (int nc = -1; nc <= 1; nc += 2) {
	                    int newR = i + direction;  
	                    int newC = j + nc;
	                    
	                    if (isValidPosition(newR, newC) && isValidMove(i, j, newR, newC)) {
	                        validMove[moveCount][0] = i;
	                        validMove[moveCount][1] = j;
	                        validMove[moveCount][2] = newR;
	                        validMove[moveCount][3] = newC;
	                        moveCount++;
	                    }
	                }
	            }
	        }
	    }
	    return moveCount > 0 ? validMove[(int)(Math.random() * moveCount)] : null;
	}
}