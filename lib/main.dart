import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/history_screen.dart';
import 'package:sahamku/ui/screens/pasar_screen.dart';
import 'package:sahamku/ui/screens/portofolio_screen.dart';
import 'package:sahamku/ui/screens/search_screen.dart';
import 'package:sahamku/ui/screens/settings_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const SahamKuApp());
}

class SahamKuApp extends StatelessWidget {
  const SahamKuApp({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => MainViewModel(),
      child: Consumer<MainViewModel>(
        builder: (context, viewModel, child) {
          return MaterialApp(
            title: 'SahamKu',
            debugShowCheckedModeBanner: false,
            themeMode: viewModel.currentThemeMode,
            theme: ThemeData(
              useMaterial3: true,
              brightness: Brightness.light,
              colorSchemeSeed: const Color(0xFF10B981),
              scaffoldBackgroundColor: const Color(0xFFF8FAFC),
            ),
            darkTheme: ThemeData(
              useMaterial3: true,
              brightness: Brightness.dark,
              colorSchemeSeed: const Color(0xFF10B981),
              scaffoldBackgroundColor: const Color(0xFF0F172A),
              cardTheme: const CardThemeData(color: Color(0xFF1E293B)),
            ),
            home: const MainNavigationContainer(),
          );
        },
      ),
    );
  }
}

class MainNavigationContainer extends StatefulWidget {
  const MainNavigationContainer({super.key});

  @override
  State<MainNavigationContainer> createState() => _MainNavigationContainerState();
}

class _MainNavigationContainerState extends State<MainNavigationContainer> {
  int _currentIndex = 0;

  final List<Widget> _screens = const [
    PasarScreen(),
    PortofolioScreen(),
    HistoryScreen(),
    SettingsScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _currentIndex,
        children: _screens,
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: const Color(0xFF10B981),
        shape: const CircleBorder(),
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (_) => const SearchScreen()),
          );
        },
        child: const Icon(Icons.search, color: Colors.white),
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.show_chart),
            selectedIcon: Icon(Icons.show_chart, color: Color(0xFF10B981)),
            label: 'Pasar',
          ),
          NavigationDestination(
            icon: Icon(Icons.account_balance_wallet_outlined),
            selectedIcon: Icon(Icons.account_balance_wallet, color: Color(0xFF10B981)),
            label: 'Portofolio',
          ),
          NavigationDestination(
            icon: Icon(Icons.history_outlined),
            selectedIcon: Icon(Icons.history, color: Color(0xFF10B981)),
            label: 'Riwayat',
          ),
          NavigationDestination(
            icon: Icon(Icons.settings_outlined),
            selectedIcon: Icon(Icons.settings, color: Color(0xFF10B981)),
            label: 'Pengaturan',
          ),
        ],
      ),
    );
  }
}
