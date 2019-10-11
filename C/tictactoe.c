#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

/**
 * 
 * index0 = x + y * size
 * x = index0 % size
 * y = (int) (index0 / size)
 * 
 * (0,0) (1,0) (2,0)
 * (0,1) (1,1) (2,1)
 * (0,2) (1,2) (2,2)
 * 
 * Flip across diagonal from top left to bottom right \
 * 
 * index1 = y + x * size
 * (x1, y1)
 * index1 = ((int)(index0/size)) + (index0 % size) * size
 * x1 = index1 % size;
 * y1 = (int) (index1 / size);
 * 
 * (0,0) (0,1) (0,2)
 * (1,0) (1,1) (1,2)
 * (2,0) (2,1) (2,2)
 * 
 * Flip across y axis |
 * 
 * 
 * x2 = size - x1 + 1
 * y2 = y1
 * nexIndex2 = x2 + y1 * size
 * 
 * (0,2) (0,1) (0,0)
 * (1,2) (1,1) (1,0)
 * (2,2) (2,1) (2,0)
 *  
 **/

enum Result
{
    Win = 0,
    Lose = 1,
    Tie = 2,
    Neutral = 3,
};

enum Piece
{
    P1 = 'X',
    P2 = 'O',
    Empty = '_',
};

typedef struct Board
{
    int size, length;
    char spots[9];
    enum Result result;
    enum Piece currentTurn;
} Board;


typedef struct Bot
{
    int gamesRecorded;
    Board history[100];
} Bot;

#define DEFAULT_BOARD                  \
    {                                  \
        3, 9, "_________", Neutral, P1 \
    }

#define DEFAULT_BOT        \
    {                      \
        .gamesRecorded = 0 \
    }

#define GAME_FILE "./gameFile.data"
#define BOT_FILE "./botFile.data"

void printBoard(Board *b);
void rotateLeft(Board *b);
void rotateRight(Board *b);
int boardsAreIdentical(Board *b1, Board *b2);
int boardsAreSimilar(Board *b1, Board *b2);
void selectSpot(Board *b, int position);
void recordBoard(Board *b);
void loadBoard(Board *b);
int getRandomSpot(Board *b);
enum Result determineResult(Board *b);
void recordBot(Bot *b);
void loadBot(Bot *b);
void addBoard(Bot *bot, Board *b);

int main(int argc, char *argv[])
{
    int inputPosition = -1;
    // Initialize board
    Board b1 = DEFAULT_BOARD;
    Bot bot = DEFAULT_BOT;
    // Load board from data
    loadBoard(&b1);
    loadBot(&bot);
    // printBoard(&b1);
    int useBot = 0;
    // Use the bot if arg 1 or 2 is -c
    if (argc > 2 && (!strcmp(argv[2], "-c") || !strcmp(argv[1], "-c")))
        useBot = 1;

    printf("%d\n", bot.gamesRecorded);
    // printBoard(&bot.history[bot.gamesRecorded]);

    // if (argc == 1)
    // {
    //     for (int i = 0; i < bot.gamesRecorded; i++)
    //     {
    //         printBoard(&bot.history[i]);
    //     }
    // }

    if (argc > 1) {
        inputPosition = atoi(argv[1]);
        // Only allow empty spots to be taken
        if (b1.spots[inputPosition] == Empty)
        {
            selectSpot(&b1, inputPosition);
            // printBoard(&b1);
            b1.result = determineResult(&b1);

            // Add the board
            int copyOver = 1;
            for (int i = 0; i < bot.gamesRecorded; i++)
            {
                if (boardsAreSimilar(&bot.history[i], &b1))
                {
                    copyOver = 0;
                    break;
                }
            }

            if (copyOver){
                printf("Copying the board\n");
                bot.history[bot.gamesRecorded].currentTurn = b1.currentTurn;
                bot.history[bot.gamesRecorded].length = b1.length;
                bot.history[bot.gamesRecorded].size = b1.size;
                for (int j = 0; j<9; j++){
                    bot.history[bot.gamesRecorded].spots[j] = b1.spots[j];
                }
                bot.history[bot.gamesRecorded].result = b1.result;
                printBoard(&bot.history[bot.gamesRecorded]);
            }

            bot.gamesRecorded++;
            recordBot(&bot);
            recordBoard(&b1);
            if (b1.result == Win)
            {
                printf("You Won!!\n");
            }
            else if (b1.result == Lose)
            {
                printf("You Lost!!\n");
            }
            else if (b1.result == Tie)
            {
                printf("You Tied!!\n");
            }
        }
        else
        {
            printBoard(&b1);
            printf("That spot is taken\n");
        }
    }

    return 0;
}

/**
 *  Returns:
 *  Result.Win if P1 wins
 *  Result.Lose if P2 wins
 *  Result.tie if it's a tie
 *  Result.Neutral if no one has won yet
 *   
 **/
enum Result determineResult(Board *b)
{
    enum Result result = Neutral;
    for (int i = 0; i < 4; i++)
    {
        // If already decided return to original position
        rotateRight(b);
        if (result != Neutral)
            continue;
        // Straight across the top
        if (b->spots[0] != Empty)
        {
            if (b->spots[0] == b->spots[1] && b->spots[1] == b->spots[2])
            {
                // Straight across the top
                if (b->spots[0] == P1)
                    result = Win;
                if (b->spots[0] == P2)
                    result = Lose;
            }
            else if (b->spots[0] == b->spots[4] && b->spots[4] == b->spots[8])
            {
                // Diagonally across
                if (b->spots[0] == P1)
                    result = Win;
                if (b->spots[0] == P2)
                    result = Lose;
            }
        }
        else if (b->spots[1] != Empty)
        {
            // Second to top left isn't Empty
            if (b->spots[1] == b->spots[4] && b->spots[4] == b->spots[7])
            {
                if (b->spots[1] == P1)
                    result = Win;
                if (b->spots[1] == P2)
                    result = Lose;
            }
        }
    }

    if (result == Neutral && getRandomSpot(b) == -1)
        result = Tie;
    return result;
}

void recordBoard(Board *b)
{
    // "rb", "wb", "ab", "rb+", "r+b", "wb+", "w+b", "ab+", "a+b"
    FILE *gameFile = fopen(GAME_FILE, "wb");
    fwrite(b, sizeof(struct Board), 1, gameFile);
    fclose(gameFile);
}

/**
 * Loads a board from the game file
 * if it is present.
**/
void loadBoard(Board *b)
{
    FILE *gameFile = fopen(GAME_FILE, "rb");
    if (gameFile == NULL)
    {
        // return 0;
    }
    else
    {
        fread(b, sizeof(struct Board), 1, gameFile);
    }
    fclose(gameFile);
    // return 1;
}

void recordBot(Bot *b)
{
    // "rb", "wb", "ab", "rb+", "r+b", "wb+", "w+b", "ab+", "a+b"
    FILE *botFile = fopen(BOT_FILE, "wb");
    fwrite(b, sizeof(struct Bot), 1, botFile);
    fclose(botFile);
}

void loadBot(Bot *b)
{
    int numBoards;
    FILE *botFile = fopen(BOT_FILE, "rb");
    // Board *board = (Board *) malloc(sizeof(board));
    if (botFile == NULL)
    {
        // return 0;
    }
    else
    {
        // fscanf (botFile, "%d", &numBoards);
        // fread(board, sizeof(struct Board), 1, botFile);
        fread(b, sizeof(struct Bot), 1, botFile);
    }
    fclose(botFile);
}

void addBoard(Bot *bot, Board *b)
{
    for (int i = 0; i < bot->gamesRecorded; i++)
    {
        if (!boardsAreSimilar(&bot->history[i], b))
        {
            bot->history[bot->gamesRecorded] = *b;
        }
    }
}

/**
 *  Prints the board in an easy to read manner 
 **/
void printBoard(Board *b)
{
    for (int i = 0; i < b->length; i += 3)
        printf("%c\t%c\t%c\n", b->spots[i], b->spots[i + 1], b->spots[i + 2]);
}

/**
 * Returns a random available spot on the board
 * 
 * Returns -1 if no spots are available. 
 **/
int getRandomSpot(Board *b)
{
    srand(time(0));

    int openSpots[9] = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    int index = 0;

    for (int i = 0; i < b->length; i++)
    {
        if (b->spots[i] == Empty)
        {
            openSpots[index] = i;
            index++;
        }
    }

    int val = rand() % index;

    return openSpots[val];
}

/**
 * Initiates a turn for the current player
 * 
 * The chosen position will be set to character of 
 * the current player's piece. 
 **/
void selectSpot(Board *b, int position)
{
    b->spots[position] = b->currentTurn;
    if (b->currentTurn == P1)
    {
        b->currentTurn = P2;
    }
    else
    {
        b->currentTurn = P1;
    }
}

/**
 * Checks all rotations of the boards
 * if two are the same then the boards are the same
 * 
 * returns 1 if they are the same 0 if they are not
 */
int boardsAreSimilar(Board *b1, Board *b2)
{
    int isSame = 0;
    // Loop through 4 times so they end up in the same orientation
    for (int i = 0; i < 4; i++)
    {
        // Rotate the board 90 degrees
        rotateRight(b1);

        // Check if flag is already set
        // if not check if they are identical in this configuration
        if (!isSame && boardsAreIdentical(b1, b2))
        {
            // Set flag to true
            isSame = 1;
        }
    }
    // Return the result.
    return isSame;
}

/**
 * Checks to see if the boards are exactly the same.
 * 
 * returns 1 if they are, 0 if not
 */
int boardsAreIdentical(Board *b1, Board *b2)
{
    for (int i = 0; i < b1->length; i++)
    {
        if (b1->spots[i] != b2->spots[i])
        {
            return 0;
        }
    }
    return 1;
}

/**
 * Rotates the board 90° to the left (CCW)
 * 
 * TODO: reduce from using second loop
 * Note: reassigns spots pointer
 **/
void rotateLeft(Board *b)
{
    char *newSpots = malloc(sizeof(char) * b->size * b->size);
    int newIndex;
    for (int i = 0; i < b->size * b->size; i++)
    {
        // Rotates to the left, counter clock wise
        newIndex = b->size - (int)(i / b->size) - 1 + (i % b->size) * b->size;
        newSpots[i] = b->spots[newIndex];
    }
    for (int i = 0; i < b->length; i++)
    {
        b->spots[i] = newSpots[i];
    }
}

/**
 * Rotates the board 90° to the right (CW)
 * 
 * Note: reassigns spots pointer
 * TODO: reduce from using second loop
 **/
void rotateRight(Board *b)
{
    char *newSpots = malloc(sizeof(char) * b->size * b->size);
    int newIndex;
    for (int i = 0; i < b->size * b->size; i++)
    {
        // Rotates to the left, counter clock wise
        newIndex = (int)(i / b->size) + (b->size - (i % b->size) - 1) * b->size;
        newSpots[i] = b->spots[newIndex];
    }
    for (int i = 0; i < b->length; i++)
    {
        b->spots[i] = newSpots[i];
    }
}