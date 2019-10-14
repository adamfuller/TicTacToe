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
    P2 = 'O', // CPU should be P2
    Empty = '_',
};

typedef struct Board
{
    int size, length, weight;
    char spots[9];
    enum Result result;
    enum Piece currentTurn;
} Board;

typedef struct Bot
{
    int gamesRecorded;
    Board history[20000]; // 19683 possible board permutations
} Bot;

#define DEFAULT_BOARD                     \
    {                                     \
        3, 9, 0, "_________", Neutral, P1 \
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
void printResult(Board *b);
int boardContains(Board *b1, Board *b2);
int isNextInRoute(Board *b1, Board *b2);
void printWeight(Board *b);
int playARound(Board *b, Bot *bot, int position);
void reorder(char arr[], int index[], int n);
int getNextMove(Board *b1, Board *b2);
int getBestNextMove(Bot *bot, Board *b);
void test();

void test()
{
    Board b1 = DEFAULT_BOARD;
    Board b2 = DEFAULT_BOARD;
    Bot bot = DEFAULT_BOT;
    // loadBot(&bot);

    // selectSpot(&b2, 2);

    // printf("Board contains: %d\n", boardContains(&b1, &b2));
    // printf("isNextInRoute: %d\n", isNextInRoute(&b1, &b2));
    // printf("Next move: %d\n", getNextMove(&b1, &b2));
    // printf("Best Next move: %d\n", getBestNextMove(&bot, &b2));

    // Load board from data
    loadBoard(&b1);
    loadBot(&bot);
    int randSpot;
    for (int i = 0; i < 100000; i++)
    {
        while (!playARound(&b1, &bot, randSpot))
        {
            randSpot = getRandomSpot(&b1);
        }
    }
}

int main(int argc, char *argv[])
{
    if (argc > 1 && !strcmp(argv[1], "-t"))
    {
        test();
        return 0;
    }
    if (argc > 1 && !strcmp(argv[1], "-n"))
    {
        // Load the default board into the current board.
        Board b2 = DEFAULT_BOARD;
        recordBoard(&b2);
        return 0;
    }
    int inputPosition = -1;
    // Initialize board
    Board b1 = DEFAULT_BOARD;
    Bot bot = DEFAULT_BOT;
    // Load board from data
    loadBoard(&b1);
    loadBot(&bot);

    // Use the bot if arg 1 or 2 is -c
    if (argc > 1 && (!strcmp(argv[1], "-c") || (argc > 2 && !strcmp(argv[2], "-c"))))
    {
        // User input a position
        if (argc > 2)
        {
            inputPosition = atoi(argv[1]);
            if (playARound(&b1, &bot, inputPosition) == -1)
            {
                return 0;
            }
            if (b1.result == Lose)
            {
                printf("You lost!\n");
                return 0;
            }
            if (b1.result == Win)
            {
                printf("You won!\n");
                return 0;
            }
            if (b1.result == Tie)
            {
                printf("It's a tie!\n");
                return 0;
            }
            printf("Computer chose:\n");
        }

        int nextMove = getBestNextMove(&bot, &b1);
        if (nextMove == -1)
        {
            nextMove = getRandomSpot(&b1);
        }
        playARound(&b1, &bot, nextMove);
        if (b1.result == Lose)
        {
            printf("You lost!\n");
        }
        if (b1.result == Win)
        {
            printf("You won!\n");
        }
        if (b1.result == Tie)
        {
            printf("It's a tie!\n");
        }
        return 0;
    }

    // Prints the number of boards contained in the bot
    if (argc > 1 && !strcmp(argv[1], "-i"))
    {
        printf("%d boards recorded\n", bot.gamesRecorded);
        return 0;
    }

    // printBoard(&bot.history[bot.gamesRecorded-1]);

    if (argc > 1)
    {
        inputPosition = atoi(argv[1]);
        playARound(&b1, &bot, inputPosition);
        if (b1.result == Lose)
            printf("You lost!\n");
        if (b1.result == Win)
            printf("You won!\n");
        if (b1.result == Tie)
            printf("It's a tie!\n");
    }
    else
    {
        // Output all recorded boards
        for (int i = 0; i < bot.gamesRecorded; i++)
        {
            printBoard(&bot.history[i]);
            printResult(&bot.history[i]);
            printf("Weight: ");
            printWeight(&bot.history[i]);
        }
    }

    return 0;
}

/**
 * Plays a round, current player puts a piece
 * in selected position
 * Returns 0 when a game is completed 
 **/
int playARound(Board *b, Bot *bot, int position)
{
    int returnVal = 0;

    // If the board is already completed start a new one
    if (b->result != Neutral)
    {
        // TODO: Uncomment later
        // printf("Starting new game\n");
        Board b2 = DEFAULT_BOARD;
        // Uncomment later
        recordBoard(&b2);
        // Uncomment later
        loadBoard(b);
    }

    // Only allow empty spots to be taken
    if (b->spots[position] == Empty && position < 9 && b->result == Neutral)
    {
        selectSpot(b, position);
        b->result = determineResult(b);
        if (b->result != Neutral)
            returnVal = 1;
        if (b->result == Lose)
            b->weight = -1;
        if (b->result == Win)
            b->weight = 1;

        // Uncomment later
        recordBoard(b);

        // Determine if the board should be saved
        int copyOver = 1;
        for (int i = 0; i < bot->gamesRecorded; i++)
        {
            if (boardsAreSimilar(&bot->history[i], b))
            {
                copyOver = 0;
                break;
            }
        }

        // Add the board to the bot's history
        if (copyOver)
        {
            printf("Copying the board\n");
            // Copy the board into the history
            bot->history[bot->gamesRecorded].currentTurn = b->currentTurn;
            bot->history[bot->gamesRecorded].length = b->length;
            bot->history[bot->gamesRecorded].size = b->size;
            for (int j = 0; j < 9; j++)
                bot->history[bot->gamesRecorded].spots[j] = b->spots[j];

            bot->history[bot->gamesRecorded].result = b->result;
            bot->history[bot->gamesRecorded].weight = b->weight;

            // We will always print at the end, uncomment for testing
            // printBoard(&bot->history[bot->gamesRecorded]);

            /** For all boards in bot history
                 * check if it is a subboard
                 *  if it is add the weight
                 * */
            for (int index = 0; index < bot->gamesRecorded; index++)
            {
                if (boardContains(b, &bot->history[index]))
                {
                    bot->history[index].weight += bot->history[bot->gamesRecorded].weight;
                }
            }

            bot->gamesRecorded++;
            // Uncomment later
            printf("Boards recorded: %d\n", bot->gamesRecorded);
            recordBot(bot);
        }
        else
        {
            // Uncomment later
            // printBoard(b);
        }
        // Uncomment later
        // printResult(b);
    }
    else
    {
        // Uncomment later
        // printBoard(b);
        if (b->result == Neutral)
        {
            // Uncomment later
            printf("That spot is taken\n");
            returnVal = -1;
            printBoard(b);
        }
        else
        {
            // Uncomment later
            // printf("The game is over\n");
        }
    }

    printBoard(b);
    return returnVal;
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

void printResult(Board *b)
{
    if (b->result == Win)
    {
        printf("You Won!!\n");
    }
    else if (b->result == Lose)
    {
        printf("You Lost!!\n");
    }
    else if (b->result == Tie)
    {
        printf("You Tied!!\n");
    }
    else if (b->result == Neutral)
    {
        printf("Neutral\n");
    }
}

void printWeight(Board *b)
{
    printf("%d\n", b->weight);
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

    // Note: DO NOT REMOVE!
    // Handles following error:
    // Floating point exception: 8
    if (index == 0)
        return -1;

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
 * Checks to see if b1 contains b2
 * 
 * returns 1 if b2 is a sub-board of b1
 * returns 0 otherwise
 * 
 * Note: b2 is a sub-board of b1 if it is 
 * possible during gameplay to go from b2 to b1
 * for any orientation of b2.
 **/
int boardContains(Board *b1, Board *b2)
{
    int output = 0;

    // Rotate 4 times to negate changes.
    for (int index = 0; index < 4; index++)
    {
        rotateRight(b1);
        // If output is already set just keep rotating
        if (output)
            continue;
        for (int i = 0; i < b1->length; i++)
        {
            if (b2->spots[i] != Empty && b2->spots[i] != b1->spots[i])
                break;
            output = 1;
        }
    }

    return output;
}

/**
 * Checks to see if b1 contains b2
 * 
 * returns 1 if b2 is a sub-board of b1
 * returns 0 otherwise
 * 
 * Note: b2 is a sub-board of b1 if it is 
 * possible during gameplay to go from b2 to b1
 * for the CURRENT orientations.
 **/
int boardContainsDirect(Board *b1, Board *b2)
{
    for (int i = 0; i < b1->length; i++)
    {
        if (b2->spots[i] != Empty && b2->spots[i] != b1->spots[i])
            return 0;
    }
    return 1;
}

/**
 * Determines if b2 is one move after b1
 * 
 * Returns 1 if b2 is achievable in one move from b1
 * Returns 0 otherwise 
 **/
int isNextInRoute(Board *b1, Board *b2)
{
    int numDiff = 0;
    int output = 0;
    // Rotate 4 times to negate changes.
    for (int index = 0; index < 4; index++)
    {
        rotateRight(b1);
        // Don't keep checking if it's already true.
        if (output)
            continue;

        for (int i = 0; i < b1->length; i++)
        {
            // Make sure the spot is not empty in b2.
            if (b2->spots[i] != Empty && b2->spots[i] != b1->spots[i] && b2->spots[i] == b1->currentTurn)
            {
                numDiff++;
                if (numDiff > 1)
                {
                    numDiff = 0;
                    break;
                }
            }
        }
        // If this orientation only has one difference it is the next in line
        if (numDiff == 1)
            output = 1;
    }

    return output;
}

/**
 * Returns the index of the position to take to go from
 * b1 to b2 if nextInRoute is true for b1 to b2.
 * 
 * returns -1 if going from b1 to b2 in one move isn't possible
 * or if the move can not be determined
 **/
int getNextMove(Board *b1, Board *b2)
{
    if (!isNextInRoute(b1, b2))
    {
        return -1;
    }
    for (int index = 0; index < 4; index++)
    {
        if (!boardContainsDirect(b2, b1))
        {
            continue;
        }
        for (int i = 0; i < b1->length; i++)
        {
            if (b1->spots[i] != b2->spots[i])
                return i;
        }
        rotateRight(b2);
    }

    return -1;
}

/**
 * Returns the index of the best move know
 * to the bot. 
 * 
 * If no move can be chosen a random one is used.
 * If no move is available, -1 is returned.
 **/
int getBestNextMove(Bot *bot, Board *b)
{
    int bestWeight = 0;
    int bestNextMove = getRandomSpot(b);
    for (int i = 0; i < bot->gamesRecorded; i++)
    {
        if (!isNextInRoute(b, &bot->history[i]))
            continue;
        if (((b->currentTurn == P2 && bot->history[i].weight < bestWeight) || (b->currentTurn == P1 && bot->history[i].weight > bestWeight)) || bestNextMove == -1)
        {
            bestWeight = bot->history[i].weight;
            bestNextMove = getNextMove(b, &bot->history[i]);
        }
    }
    return bestNextMove;
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
    // Don't forget to free up memory...
    free(newSpots);
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
    // Don't forget to free up memory...
    free(newSpots);
}