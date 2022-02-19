part of app;

class MainView extends StatefulWidget {
  MainView();

  _MainViewState createState() => new _MainViewState();
}

class _MainViewState extends State<MainView> {
  MainViewModel vm;

  @override
  void initState() {
    vm = new MainViewModel(() {
      if (mounted) {
        setState(() {});
      }
    });

    super.initState();
  }

  @override
  void dispose() {
    vm.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: Center(
          child: Text(
            vm.board.currentPiece,
            style: TextStyle(fontSize: 25),
          ),
        ),
        title: Text(vm.board.getWinner() != 0 ? "Winner: ${vm.board.getWinnerPiece()}" : "${vm.board.isFinished() ? "Tie" : ""}"),
        actions: <Widget>[
          IconButton(
            icon: Icon(Icons.looks_one),
            onPressed: vm.stepPressed,
          ),
          IconButton(
            icon: Icon(Icons.shuffle),
            onPressed: vm.randomPressed,
          ),
          IconButton(
            icon: Icon(Icons.skip_next),
            onPressed: vm.isRunning ? null : vm.runPressed,
          ),
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: vm.resetPressed,
          ),
        ],
      ),
      body: _getBody(),
    );
  }

  Widget _getBody() {
    return ListView(
      children: [
        Row(
          children: <Widget>[
            Text("Run"),
            Flexible(
              child: TextField(
                controller: vm.runTimeController,
                textAlign: TextAlign.center,
                keyboardType: TextInputType.numberWithOptions(decimal: false, signed: false),
                onChanged: (s) {},
              ),
            ),
            Text("Times"),
          ],
        ),
        Text(
          "Generation: ${vm.generationCount}",
          textAlign: TextAlign.center,
        ),
        Center(
          child: CustomWidget(
            onPointerDown: vm.positionClicked,
            onPaint: vm.paintBoard,
            doesRepaint: false,
            size: MediaQuery.of(context).size.shortestSide - 50,
          ),
        ),
        // NetworkMap(
        //   network: vm.networks.first,
        // ),
        Text(vm.generationIntroduced.entries.take(10).fold("", (s, e) {
          s += "${e.key.hashCode}:${e.value}";
          s += "\n";
          return s;
        })),
      ],
    );
  }
}
