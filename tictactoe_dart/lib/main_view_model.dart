part of app;

class MainViewModel {
  //
  // Private members
  //
  List<StreamSubscription> _listeners;
  // Network _network = Network(
  //   [9, 10, 10, 9],
  //   activationFunction: ActivationFunction.leakyRelu,
  // );
  Paint _boardPaint = ((Paint()..color = Colors.black)..strokeWidth = 4.0)..strokeCap = StrokeCap.round;
  Size _boardSize;
  Board board = Board();

  static int networksPerGeneration = 45;
  static int populationCarryOver = 15;
  List<Network> networks = List<Network>();
  List<Network> carryOver = List<Network>();
  Map<Network, int> wins = Map<Network, int>();
  Map<Network, int> generationIntroduced = Map<Network, int>();
  int generationCount = 0;
  bool isRunning = false;
  Timer trainingTimer;

  //
  // Public Properties
  //
  Function onDataChanged;
  bool isLoading = true;

  TextEditingController runTimeController = TextEditingController(text: "1");

  //
  // Getters
  //
  Network get _standardNetwork => Network([10, 10, 9], activationFunction: ActivationFunction.sigmoid);

  //
  // Constructor
  //
  MainViewModel(this.onDataChanged) {
    init();
  }

  //
  // Public functions
  //
  void init() async {
    if (_listeners == null) _attachListeners();

    //
    // Write any initializing code here
    //
    Network.mutationFactor = 0.1;
    for (int i = 0; i < networksPerGeneration; i++) {
      this.networks.add(_standardNetwork);
      wins[networks[i]] = 0;
      generationIntroduced[networks[i]] = 0;
    }

    this.isLoading = false;
    onDataChanged();
  }

  void nextGeneration() {
    List<Network> keys = generationIntroduced.keys.toList();
    List<int> values = generationIntroduced.values.toList();
    generationIntroduced.clear();
    for (int i = 0; i < keys.length; i++) {
      if (networks.indexOf(keys[i]) != -1) {
        // Remove n from generationIntroduced
        generationIntroduced[keys[i]] = values[i];
      }
    }
    this.networks = carryOver ?? [_standardNetwork];
    generationCount++;
    for (int i = networks.length; i < networksPerGeneration; i++) {
      if (i % (networks.length + 1) == 0) {
        networks.add(_standardNetwork);
      } else {
        networks.add(networks[i % networks.length].produceMutation());
      }
      generationIntroduced[networks.last] = generationCount;
    }
  }

  void stepPressed() {
    // Pick what should be the best Network to make a move
    board.networkSelect(networks.first);
    onDataChanged();
  }

  void runPressed() async {
    if (isRunning){ 
      trainingTimer.cancel();
      isRunning = false;
      onDataChanged();
      return;
    }
    onDataChanged();
    int runCount = 1;
    int currentRun = 0;
    if (runTimeController.text.isNotEmpty) runCount = int.tryParse(runTimeController.text) ?? 1;
    // ReceivePort rp = ReceivePort();
    // Isolate isolate = await Isolate.spawn((message) {
      
    // }, rp.sendPort);
    // rp.listen((message) {
      
    // });
    trainingTimer = Timer.periodic(Duration(milliseconds: 100), (timer) {
      currentRun++;
      trainNetworks();
      if (currentRun == runCount) {
        timer.cancel();
        isRunning = false;
        onDataChanged();
      }
      if (currentRun % 5 == 0) onDataChanged();
    });
    // for (int i = 0; i < runCount; i++) trainNetworks();
    // onDataChanged();
  }

  void trainNetworks() {
    // Reset the win counts
    wins.clear();

    networks.forEach((n) {
      // Assign a 0 for all current networks
      wins[n] = 0;
      for (int j = 0; j < 30; j++) {
        // Network n = networks[i];
        // Network n2 = networks[j];
        // Make sure a clear board is being used
        board.reset();

        // Play until the game is finished
        while (!board.isFinished()) {
          // Make a random move
          board.randomSelect(tryToWin: false);
          // Have network2 pick as player 1
          // board.networkSelect(n2);
          // Have the network pick a spot, network is player 2
          if (!board.isFinished()) board.networkSelect(n);
        }
        switch (board.getWinner()) {
          // If the network tied count it as a win
          case 0:
            wins[n] += 1;
            break;
          // If the network won tick up the wins
          case -1:
            wins[n] += 1;
            break;
          case 1:
            wins[n] -= 1;
            break;
          default:
            break;
        }

        // Now make the network play first
        board.reset();

        // Play until the game is finished
        while (!board.isFinished()) {
          board.networkSelect(n);
          board.randomSelect(tryToWin: false);
        }

        switch (board.getWinner()) {
          // If the network tied count it as a win
          case 0:
            wins[n] += 1;
            break;
          // If the network won tick up the wins
          case -1:
            wins[n] -= 1;
            break;
          case 1:
            wins[n] += 1;
            break;
          default:
            break;
        }
      }
    });

    // Sort networks by wins
    networks.sort((a, b) => wins[b].compareTo(wins[a]));

    // Hold onto the top networks by wins
    carryOver = networks.take(MainViewModel.populationCarryOver).toList();
    // Start the next generation
    nextGeneration();
  }

  void randomPressed() {
    board.randomSelect();
    onDataChanged();
  }

  void positionClicked(double x, double y) {
    int posX = (x / _boardSize.width * 3).floor();
    int posY = (y / _boardSize.height * 3).floor();
    // print("posX : $posX, posY : $posY");
    board.pickSpot(board.spotFromXY(posX, posY));
    // print(board.getWinningSpot());
    onDataChanged();
  }

  void resetPressed() {
    board.reset();
    onDataChanged();
  }

  void paintBoard(Canvas c, Size s, double d) {
    _boardSize = s;
    double spacing = s.width / 3;
    double gap = 10;
    for (int i = 1; i < 3; i++) {
      double start = spacing * i;
      c.drawLine(Offset(start, gap), Offset(start, s.height - gap), _boardPaint);
      c.drawLine(Offset(gap, start), Offset(s.width - gap, start), _boardPaint);
    }

    for (int i = 0; i < board.positions.length; i++) {
      String piece = board.getPiece(i);
      if (piece == null) continue;

      double x = spacing * (i % 3) + spacing / 4;
      double y = spacing * (i / 3).floor() + spacing / 4;
      TextSpan ts = TextSpan(
        text: piece,
        style: new TextStyle(
          color: piece == board.piece1 ? board.piece1Color : board.piece2Color,
          fontSize: spacing / 2,
        ),
      );
      TextPainter tp = TextPainter(
        text: ts,
        textDirection: TextDirection.ltr,
      );
      tp.layout();
      tp.paint(c, Offset(x, y));
    }
  }

  //
  // Private functions
  //
  void _attachListeners() {
    if (this._listeners == null) {
      this._listeners = [
        //
        // Put listeners here
        //
      ];
    }
  }

  //
  // Dispose
  //
  void dispose() {
    this._listeners?.forEach((_) => _.cancel());
  }
}
