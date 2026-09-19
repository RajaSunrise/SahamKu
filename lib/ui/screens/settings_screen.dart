import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:sahamku/ui/main_viewmodel.dart';
import 'package:sahamku/ui/screens/calculator_screen.dart';
import 'package:sahamku/ui/screens/rekomendasi_screen.dart';
import 'package:sahamku/ui/screens/screener_screen.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final viewModel = Provider.of<MainViewModel>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Pengaturan & Alat', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          const Text('Tampilan & Tema', style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          RadioListTile<AppThemeMode>(
            title: const Text('Mode Gelap (Dark Mode)'),
            value: AppThemeMode.dark,
            groupValue: viewModel.themeMode,
            activeColor: const Color(0xFF10B981),
            onChanged: (val) {
              if (val != null) viewModel.setThemeMode(val);
            },
          ),
          RadioListTile<AppThemeMode>(
            title: const Text('Mode Terang (Light Mode)'),
            value: AppThemeMode.light,
            groupValue: viewModel.themeMode,
            activeColor: const Color(0xFF10B981),
            onChanged: (val) {
              if (val != null) viewModel.setThemeMode(val);
            },
          ),
          RadioListTile<AppThemeMode>(
            title: const Text('Sistem (Default Device)'),
            value: AppThemeMode.system,
            groupValue: viewModel.themeMode,
            activeColor: const Color(0xFF10B981),
            onChanged: (val) {
              if (val != null) viewModel.setThemeMode(val);
            },
          ),
          const Divider(height: 32),

          const Text('Fitur Analisis & Alat', style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          ListTile(
            leading: const Icon(Icons.recommend, color: Color(0xFF10B981)),
            title: const Text('Rekomendasi Analis'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (_) => const RekomendasiScreen()));
            },
          ),
          ListTile(
            leading: const Icon(Icons.filter_list, color: Color(0xFF10B981)),
            title: const Text('Screener Saham'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (_) => const ScreenerScreen()));
            },
          ),
          ListTile(
            leading: const Icon(Icons.calculate, color: Color(0xFF10B981)),
            title: const Text('Kalkulator Investasi'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {
              Navigator.push(context, MaterialPageRoute(builder: (_) => const CalculatorScreen()));
            },
          ),
          const Divider(height: 32),

          const Text('Data Portofolio Demo', style: TextStyle(color: Colors.grey, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          ListTile(
            leading: const Icon(Icons.restore, color: Color(0xFFEF4444)),
            title: const Text('Reset Portofolio Demo', style: TextStyle(color: Color(0xFFEF4444))),
            subtitle: const Text('Mengembalikan saldo awal \$10,000 & menghapus riwayat transaksi.'),
            onTap: () {
              showDialog(
                context: context,
                builder: (ctx) => AlertDialog(
                  title: const Text('Reset Data Portofolio?'),
                  content: const Text('Tindakan ini akan mengembalikan saldo ke \$10,000 dan menghapus seluruh posisi serta riwayat transaksi demo.'),
                  actions: [
                    TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Batal')),
                    ElevatedButton(
                      style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFEF4444)),
                      onPressed: () async {
                        await viewModel.resetAllData();
                        if (context.mounted) {
                          Navigator.pop(ctx);
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Data portofolio berhasil direset.')),
                          );
                        }
                      },
                      child: const Text('Reset', style: TextStyle(color: Colors.white)),
                    ),
                  ],
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}
