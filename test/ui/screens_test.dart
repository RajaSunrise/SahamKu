import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';
import 'package:sqflite_common_ffi/sqflite_ffi.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/pasar_screen.dart';
import 'package:sahamku/ui/screens/portofolio_screen.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  setUpAll(() {
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  });

  group('UI Screen Widget Tests', () {
    testWidgets('PasarScreen renders tabs and title', (WidgetTester tester) async {
      final viewModel = MainViewModel(autoStartRealtime: false);

      await tester.pumpWidget(
        ChangeNotifierProvider<MainViewModel>.value(
          value: viewModel,
          child: const MaterialApp(
            home: PasarScreen(),
          ),
        ),
      );

      await tester.pump();

      expect(find.text('SahamKu Pasar AS'), findsOneWidget);
      expect(find.text('Gotrade Top Movers'), findsOneWidget);
      expect(find.text('Top Gainers'), findsOneWidget);

      viewModel.dispose();
    });

    testWidgets('PortofolioScreen renders equity and position headers', (WidgetTester tester) async {
      final viewModel = MainViewModel(autoStartRealtime: false);

      await tester.pumpWidget(
        ChangeNotifierProvider<MainViewModel>.value(
          value: viewModel,
          child: const MaterialApp(
            home: PortofolioScreen(),
          ),
        ),
      );

      await tester.pump();

      expect(find.text('Total Nilai Portofolio'), findsOneWidget);
      expect(find.text('Posisi Saham Terbuka'), findsOneWidget);
      expect(find.text('Watchlist Favorit'), findsOneWidget);

      viewModel.dispose();
    });
  });
}
