import time
import random
import os, sys
'''
This is a program dedicated to creating and unbeatable tic tac toe ai
As of 21 April 2017 the player must always move first
'''
#make board as dictionary
empty_b = {1:'EMPTY',2:'EMPTY',3:'EMPTY',4:'EMPTY',
    5:'EMPTY',6:'EMPTY',7:'EMPTY',8:'EMPTY',9:'EMPTY'}

b = {1:'EMPTY',2:'EMPTY',3:'EMPTY',4:'EMPTY',
    5:'EMPTY',6:'EMPTY',7:'EMPTY',8:'EMPTY',9:'EMPTY'}


#lists all possible winning combinations
winning = [[1,2,3],[1,5,9],
            [1,4,7],[2,5,8],
            [3,6,9],[3,5,7],
            [4,5,6],[7,8,9]]

#count for who wins during automated gameplay
computer_wins, player_wins, ties = 0,0,0

#sets up player as X and computer as O
player_piece = 'X'
computer_piece = 'O'

def print_board(board):
    #prints out board
    b_to_print = []
    for k in b:
        b_to_print.append(b[k])
    #print(b_to_print)
    for x in range(0,len(b)):
        if b_to_print[x] == 'EMPTY':
            b_to_print[x] = ' '
        else:
            pass

    print('{}|{}|{}\n{}|{}|{}\n{}|{}|{}\n'.format(b_to_print[0],b_to_print[1], b_to_print[2], b_to_print[3],b_to_print[4], b_to_print[5], b_to_print[6], b_to_print[7], b_to_print[8]))


def game_is_finished(board):
    global computer_wins, player_wins, ties
    #checks to see if someone has won or if it's a tie
    for x in range(0,8):
        if any(computer_piece in board[winning[n][0]] and computer_piece in board[winning[n][1]] and computer_piece in  board[winning[n][2]] for n in range(0,8)):
            print('{} has won!'.format('The computer'))
            computer_wins +=1
            board = {1:'EMPTY',2:'EMPTY',3:'EMPTY',4:'EMPTY',
                    5:'EMPTY',6:'EMPTY',7:'EMPTY',8:'EMPTY',9:'EMPTY'}
            return True, board
            
        if any(player_piece in board[winning[n][0]] and player_piece in board[winning[n][1]] and player_piece in  board[winning[n][2]] for n in range(0,8)):
            print('You won!')
            b_to_print = []
            for k in b:
                b_to_print.append(b[k])
            #print(b_to_print)
            for x in range(0,len(b)):
                if b_to_print[x] == 'EMPTY':
                    b_to_print[x] = ' '
                else:
                    pass
            with open('/Users/alfuller/Desktop/losing_boards.txt','a') as fail:
                fail.write('{}|{}|{}\n{}|{}|{}\n{}|{}|{}\n\n'.format(b_to_print[0],b_to_print[1], b_to_print[2], b_to_print[3],b_to_print[4], b_to_print[5], b_to_print[6], b_to_print[7], b_to_print[8]))
                fail.close()
            player_wins+=1
            board = {1:'EMPTY',2:'EMPTY',3:'EMPTY',4:'EMPTY',
                    5:'EMPTY',6:'EMPTY',7:'EMPTY',8:'EMPTY',9:'EMPTY'}
            return True, board

    if all(board[place] != 'EMPTY' for place in board):
        print('It\'s a tie!')
        ties +=1
        board = {1:'EMPTY',2:'EMPTY',3:'EMPTY',4:'EMPTY',
                5:'EMPTY',6:'EMPTY',7:'EMPTY',8:'EMPTY',9:'EMPTY'}
        return True, board
    
    return False, board


def is_available_place(board, place):
    #checks if chosen place is available
    if board[place] == 'EMPTY':
        return True
    else:
        return False

        
def get_available_places(board):
    ## Returns list of open spots on board
    open_places = []
    for place in board:
        if board[place] == 'EMPTY':
            open_places.append(place)

    return open_places


def get_player_positions(board, player_piece):
    # returns list of all places taken by the player
    player_places = []
    for place in board:
        if board[place] == player_piece:
            player_places.append(place)
    return player_places


def get_computer_positions(board, computer_piece):
    #returns list of all places taken by computer
    computer_places = []
    for place in board:
        if board[place] == computer_piece:
            computer_places.append(place)
    return computer_places

def random_move(board, player_piece):
    available = get_available_places(board)
    #print(available)
    if len(available) > 1:
        spot = available[random.randint(1,len(available)-1)]
        board[spot] = player_piece
    elif len(available) == 0:
        pass
    else:
        spot = available[0]
        board[spot] = player_piece
    #print(spot)
    
    return board


def computer_turn(board, player_piece, computer_piece):
    open_places = get_available_places(board)
    player_places = get_player_positions(board, player_piece)
    computer_places = get_player_positions(board, computer_piece)
    #corresponding counter is n+2 on corners and sides
    corners = [1,3,9,7,1,3]
    sides = [2,6,8,4,2,6]

    ## if the computer moves first it will always pick position 1
    if len(player_places) == 0 and len(computer_places) == 0:
        board[1] = computer_piece
        return board

    ##Check for winning move
    if 1 in computer_places and 3 in computer_places:
        if board[2] == 'EMPTY':
            board[2] = computer_piece
            return board

    if 1 in computer_places and 9 in computer_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 1 in computer_places and 7 in computer_places:
        if board[4] == 'EMPTY':
            board[4] = computer_piece
            return board

    if 2 in computer_places and 8 in computer_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 3 in computer_places and 9 in computer_places:
        if board[6] == 'EMPTY':
            board[6] = computer_piece
            return board

    if 3 in computer_places and 7 in computer_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 4 in computer_places and 6 in computer_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 1 in computer_places and 5 in computer_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board

    if 7 in computer_places and 9 in computer_places:
        if board[8] == 'EMPTY':
            board[8] = computer_piece
            return board 

    if 1 in computer_places and 2 in computer_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board
            
    if 1 in computer_places and 4 in computer_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    if 2 in computer_places and 5 in computer_places:
        if board[8] == 'EMPTY':
            board[8] = computer_piece
            return board

    if 3 in computer_places and 6 in computer_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board

    if 3 in computer_places and 5 in computer_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board
        elif board[7] == player_piece and board[8] == player_piece:
            if board[9] == 'EMPTY':
                board[9] = computer_piece
                return board

    if 4 in computer_places and 5 in computer_places:
        if board[6] == 'EMPTY':
            board[6] = computer_piece
            return board
    if 7 in computer_places and 8 in computer_places:
        if board[9]== 'EMPTY':
            board[9] = computer_piece
            return board 
    if 2 in computer_places and 3 in computer_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 5 in computer_places and 9 in computer_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 4 in computer_places and 7 in computer_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 5 in computer_places and 8 in computer_places:
        if board[2] == 'EMPTY':
            board[2] = computer_piece
            return board

    if 6 in computer_places and 9 in computer_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board

    if 5 in computer_places and 7 in computer_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board
    if 5 in computer_places and 6 in computer_places:
        if board[4] == 'EMPTY':
            board[4] = computer_piece
            return board
    if 8 in computer_places and 9 in computer_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    ## Checks for defensive move
    if 1 in player_places and 3 in player_places:
        if board[2] == 'EMPTY':
            board[2] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 1 in player_places and 9 in player_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
        elif board[2] == 'EMPTY':
            board[2] = computer_piece
            return board

    if 8 in player_places and 9 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    if 1 in player_places and 5 in player_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board

    if 1 in player_places and 7 in player_places:
        if board[4] == 'EMPTY':
            board[4] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 2 in player_places and 8 in player_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
        elif board[6] == 'EMPTY':
            board[6] = computer_piece
            return board

    if 3 in player_places and 9 in player_places:
        if board[6] == 'EMPTY':
            board[6] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 3 in player_places and 7 in player_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
        elif board[8] == 'EMPTY':
            board[8] = computer_piece
            return board

    if 3 in player_places and 5 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    if 4 in player_places and 6 in player_places:
        if board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
        elif board[3] == 'EMPTY':
            board[3] = computer_piece
            return board

    if 7 in player_places and 9 in player_places:
        if board[8] == 'EMPTY':
            board[8] = computer_piece
            return board 
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 8 in player_places and 9 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    if 1 in player_places and 2 in player_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
            
    if 1 in player_places and 4 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board
        elif board[3] == 'EMPTY':
            board[3] = computer_piece
            return board

    if 2 in player_places and 5 in player_places:
        if board[8] == 'EMPTY':
            board[8] = computer_piece
            return board

    if 3 in player_places and 6 in player_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board
    if 3 in player_places and 5 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board
    if 4 in player_places and 5 in player_places:
        if board[6] == 'EMPTY':
            board[6] = computer_piece
            return board
    if 7 in player_places and 8 in player_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board

    if 7 in player_places and 8 in player_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board

    if 2 in player_places and 3 in player_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 5 in player_places and 9 in player_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 4 in player_places and 7 in player_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
    if 5 in player_places and 8 in player_places:
        if board[2] == 'EMPTY':
            board[2] = computer_piece
            return board

    if 6 in player_places and 9 in player_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board

    if 5 in player_places and 7 in player_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board
    if 5 in player_places and 6 in player_places:
        if board[4] == 'EMPTY':
            board[4] = computer_piece
            return board

    if 8 in player_places and 9 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board

    if 8 in player_places and 9 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board 

    if 4 in player_places and 2 in player_places:
        if board[1] == 'EMPTY':
            board[1] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 4 in player_places and 8 in player_places:
        if board[7] == 'EMPTY':
            board[7] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
            
    if 8 in player_places and 6 in player_places:
        if board[9] == 'EMPTY':
            board[9] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board

    if 2 in player_places and 6 in player_places:
        if board[3] == 'EMPTY':
            board[3] = computer_piece
            return board
        elif board[5] == 'EMPTY':
            board[5] = computer_piece
            return board
            
    if 5 in player_places and 7 in player_places:
        if board[8] == 'EMPTY':
            board[8] = computer_piece
            return board


    if 5 in player_places:
        if len(player_places) == 1:
            if board[1] == 'EMPTY':
                board[1] = computer_piece
            elif board[2] == 'EMPTY':
                board[2] = computer_piece
            elif board[9] == 'EMPTY':
                board[9] = computer_piece
            return board

        elif len(player_places) == 2:
            if 9 in player_places:
                board[3] = computer_piece

            else:
                for x in range(0,5):
                    if corners[x] in player_places:
                        position = 'corner'
                        break

                    elif sides[x] in player_places:
                        position = 'side'
                        break

                if position == 'corner':
                    if board[corners[x+2]] == computer_piece:
                        if board[sides[x+2]] != computer_piece and board[sides[x+2]] != player_piece:
                            board[sides[x+2]] = computer_piece
                    else:
                        board[corners[x+2]] = computer_piece

                elif position == 'side':
                    #board[sides[x+2]] = computer_piece
                    if board[sides[x+2]] == computer_piece:
                        if board[corners[x+2]] != computer_piece and board[corners[x+2]] != player_piece:
                            board[corners[x+2]] = computer_piece
                    else:
                        board[sides[x+2]] = computer_piece

        elif len(player_places) == 3:
            if computer_places == [1,3] and 2 not in player_places:
                board[2] = computer_piece

            elif 2 in player_places and 9 in player_places and 8 not in computer_places:
                board[8] = computer_piece

            elif 2 in player_places and 9 in player_places and 6 not in computer_places:
                board[6] = computer_piece

            elif computer_places == [1,3] and 2 in player_places:
                board[8] = computer_piece

            elif computer_places == [1,7] and 4 not in player_places:
                board[4] = computer_piece

            elif computer_places == [1,7] and 4 in player_places:
                board[6] = computer_piece
            
            elif computer_places == [1,2] and 3 not in player_places:
                board[3] = computer_piece

            elif computer_places == [1,4] and board[7] == 'EMPTY':
                board[7] = computer_piece
            

            elif computer_places == [1, 8] or computer_places == [1, 7]:
                if 4 in player_places:
                    board[6] = computer_piece
                elif 6 in player_places:
                    board[4] = computer_piece
                elif 3 in player_places and 7 not in computer_places:
                    board[7] = computer_piece
                elif 3 in player_places and 7 in computer_places:
                    board[4] = computer_piece
                elif 7 in player_places:
                    board[3] = computer_piece

            elif computer_places == [1, 6]:
                if 8 in player_places:
                    board[2] = computer_piece
                elif 7 in player_places:
                    board[3] = computer_piece
                elif 9 in player_places:
                    board[8] = computer_piece

            else:
                for x in range(0,5):
                    if corners[x] in player_places:
                        position = 'corner'
                        break

                    elif sides[x] in player_places:
                        position = 'side'
                        break

                if position == 'corner':
                    if board[corners[x+2]] == computer_piece:
                        if board[sides[x+2]] == 'EMPTY':
                            board[sides[x+2]] = computer_piece
                    else:
                        board[corners[x+2]] = computer_piece

                elif position == 'side':
                    #board[sides[x+2]] = computer_piece
                    if board[sides[x+2]] == computer_piece:
                        if board[corners[x+2]] == 'EMPTY':
                            board[corners[x+2]] = computer_piece
                    else:
                        board[sides[x+2]] = computer_piece

        elif len(player_places) == 4:
            if 2 in player_places and 7 in player_places and 8 in computer_places:
                board[6] = computer_piece
            elif 2 in player_places and 7 in player_places and 8 not in computer_places:
                board[8] = computer_piece
            elif 2 in player_places and 4 in player_places and 8 not in computer_places:
                board[8] = computer_piece
            elif 2 in player_places and 4 in player_places and 6 not in computer_places:
                board[6] = computer_piece
            elif 4 in player_places and 8 in player_places and 7 in player_places and 3 not in computer_places:
                board[3] = computer_piece
            elif 4 in player_places and 8 in player_places and 7 in player_places and 3 in computer_places:
                board[9] = computer_piece
            elif 4 in player_places and 7 in player_places and 9 in player_places:
                board[2] = computer_piece
            elif 4 in player_places and 2 in player_places and 9 in player_places and 6 not in computer_places:
                board[6] = computer_piece
            elif 2 in player_places and 9 in player_places and 3 in player_places:
                board[7] = computer_piece
            elif 2 in player_places and 9 in player_places and 4 in player_places:
                board[3] = computer_piece
            elif 2 in player_places and 9 in player_places and 6 in player_places:
                board[4] = computer_piece
            elif 2 in player_places and 3 in player_places and 6 in player_places:
                board[4] = computer_piece
        return board

    '''
    corners = [1,3,9,7,1,3]
    sides = [2,6,8,4,2,6]
    '''
    if any(corners[x] in player_places for x in range(0,5)):
        placed_piece = 0
        if len(player_places) == 1:
            if board[5] == 'EMPTY':
                board[5] = computer_piece
                placed_piece = 1

        elif placed_piece == 0:
            for x in range(0,4):
                    if corners[x] in player_places:
                        if board[corners[x+2]] == 'EMPTY' and placed_piece == 0:
                            board[corners[x+2]] = computer_piece
                            placed_piece = 1
                            break

                        elif placed_piece == 0:
                            if board[5] == 'EMPTY' and placed_piece == 0:
                                board[5] = computer_piece
                                placed_piece = 1
                                break
        if placed_piece == 1:
            return board

    if any(sides[x] in player_places for x in range(0,5)):
        placed_piece = 0
        for x in range(0,4):
                if sides[x] in player_places:
                    if board[sides[x+2]] == 'EMPTY' and placed_piece == 0:
                        board[sides[x+2]] = computer_piece
                        placed_piece = 1
                        break
                    else:
                        if board[1] == 'EMPTY':
                            placed_piece = 1
                            board[1] = computer_piece
                            break


    return board

def player_turn(board, player_piece):
    #allows for player to take their turn
    failed = True
    #print_board(b)
    while failed == True:
        try:
            move = input('Where would you like to go?\n')
        except:
            move = input('1|2|3\n4|5|6\n7|8|9\nPick a spot')
        try:
            move = int(move)
        except:
            print('Please enter a number between 1 and 9\n')
            failed = True

        if is_available_place(board, move):
            board[move] = player_piece
            #print_board(board)
            failed = False
        else:
            print('That spot is taken\n')
            failed = True
    return board



if __name__ == '__main__':
    
    
    first = input('Would you like to go first or second? (1/2)')
    #set to 'not' to play against AI set to 'random' to have random opponent
    rando = "not"
    #set to 'player' to play against it set to 'computer' to play itself
    play = 'computer'
    #time between turns
    delay = 0
    while True:
        if int(first) == 1:
            #determines if it is pVSc or cVSc
            if b == empty_b:
                print_board(b)

            if play == 'player':
                b = player_turn(b, player_piece)
            else:
                b = random_move(b, player_piece)
                time.sleep(delay)
            
            #clears screen before printing
            os.system('clear')
            print_board(b)
            _, b = game_is_finished(b)

            #determines if opponent is random
            if rando == 'random':
                b = random_move(b, computer_piece)
            elif rando == 'not':
                b = computer_turn(b, player_piece, computer_piece)

            #clears screen before printing
            os.system('clear')
            print_board(b)
            _, b = game_is_finished(b)

        else:
            #determines if opponent is random
            if rando == 'random':
                b = random_move(b, computer_piece)
            elif rando == 'not':
                b = computer_turn(b, player_piece, computer_piece)
            
            
            #clears screen before printing
            os.system('clear')
            print_board(b)
            _, b = game_is_finished(b)

            if b == empty_b:
                print_board(b)

            #determines if it is pVSc or cVSc
            if play == 'player':
                b = player_turn(b, player_piece)
            else:
                b = random_move(b, player_piece)
                time.sleep(delay)

            #clears screen before printing
            os.system('clear')
            print_board(b)
            _, b = game_is_finished(b)

            

        print('Computer wins {}\n'.format(computer_wins))
        print('Player wins {}\n'.format(player_wins))
        print('Ties {}\n'.format(ties))
    