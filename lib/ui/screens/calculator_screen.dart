import 'dart:math';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class CalculatorScreen extends StatefulWidget {
  const CalculatorScreen({super.key});

  @override
  State<CalculatorScreen> createState() => _CalculatorScreenState();
}

class _CalculatorScreenState extends State<CalculatorScreen> {
  final TextEditingController _initialController = TextEditingController(text: '1000');
  final TextEditingController _monthlyController = TextEditingController(text: '100');
  final TextEditingController _rateController = TextEditingController(text: '8.5');
  final TextEditingController _yearsController = TextEditingController(text: '10');

  double _futureValue = 0.0;
  double _totalInvested = 0.0;
  double _totalReturns = 0.0;

  @override
  void initState() {
    super.initState();
    _calculate();
  }

  void _calculate() {
    final P = double.tryParse(_initialController.text) ?? 0.0;
    final PMT = double.tryParse(_monthlyController.text) ?? 0.0;
    final rate = (double.tryParse(_rateController.text) ?? 0.0) / 100.0;
    final years = double.tryParse(_yearsController.text) ?? 0.0;

    final n = 12; // Compounded monthly
    final months = years * n;

    if (months <= 0) {
      setState(() {
        _futureValue = P;
        _totalInvested = P;
        _totalReturns = 0.0;
      });
      return;
    }

    final rMonthly = rate / n;
    double fvP = P * pow(1 + rMonthly, months);
    double fvPMT = 0.0;
    if (rMonthly > 0) {
      fvPMT = PMT * ((pow(1 + rMonthly, months) - 1) / rMonthly);
    } else {
      fvPMT = PMT * months;
    }

    final totalFV = fvP + fvPMT;
    final invested = P + (PMT * months);
    final returns = totalFV - invested;

    setState(() {
      _futureValue = totalFV;
      _totalInvested = invested;
      _totalReturns = returns;
    });
  }

  @override
  void dispose() {
    _initialController.dispose();
    _monthlyController.dispose();
    _rateController.dispose();
    _yearsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final currencyFormatter = NumberFormat.currency(symbol: '\$', decimalDigits: 2);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Kalkulator Investasi', style: TextStyle(fontWeight: FontWeight.bold)),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Results Card
            Card(
              color: const Color(0xFF1E293B),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    const Text('Estimasi Nilai Masa Depan', style: TextStyle(color: Colors.grey, fontSize: 13)),
                    const SizedBox(height: 6),
                    Text(
                      currencyFormatter.format(_futureValue),
                      style: const TextStyle(fontSize: 28, fontWeight: FontWeight.bold, color: Color(0xFF10B981)),
                    ),
                    const Divider(height: 24, color: Color(0xFF334155)),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Total Modal Investasi', style: TextStyle(color: Colors.grey, fontSize: 12)),
                            Text(
                              currencyFormatter.format(_totalInvested),
                              style: const TextStyle(fontWeight: FontWeight.bold, color: Colors.white),
                            ),
                          ],
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            const Text('Estimasi Keuntungan', style: TextStyle(color: Colors.grey, fontSize: 12)),
                            Text(
                              currencyFormatter.format(_totalReturns),
                              style: const TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF10B981)),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 24),

            // Form Inputs
            const Text('Parameter Simulasi', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 16),

            TextField(
              controller: _initialController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Modal Awal (\$ USD)',
                border: OutlineInputBorder(),
                prefixText: '\$ ',
              ),
              onChanged: (_) => _calculate(),
            ),
            const SizedBox(height: 16),

            TextField(
              controller: _monthlyController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Investasi Bulanan (\$ USD)',
                border: OutlineInputBorder(),
                prefixText: '\$ ',
              ),
              onChanged: (_) => _calculate(),
            ),
            const SizedBox(height: 16),

            TextField(
              controller: _rateController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Estimasi Return Per Tahun (%)',
                border: OutlineInputBorder(),
                suffixText: '%',
              ),
              onChanged: (_) => _calculate(),
            ),
            const SizedBox(height: 16),

            TextField(
              controller: _yearsController,
              keyboardType: const TextInputType.numberWithOptions(decimal: true),
              decoration: const InputDecoration(
                labelText: 'Jangka Waktu (Tahun)',
                border: OutlineInputBorder(),
                suffixText: 'Tahun',
              ),
              onChanged: (_) => _calculate(),
            ),
          ],
        ),
      ),
    );
  }
}
