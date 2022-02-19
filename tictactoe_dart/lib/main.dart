library app;
import 'dart:async';
import 'dart:isolate';

import 'package:custom_widget/custom_widget.dart';
import 'package:flutter/material.dart';
import 'package:neural_network/neural_network.dart';
import 'package:tictactoe/board.dart';
import 'package:tictactoe/network_map.dart';

part "main_view.dart";
part "main_view_model.dart";

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MainView(),
      debugShowCheckedModeBanner: false,
    );
  }
}
