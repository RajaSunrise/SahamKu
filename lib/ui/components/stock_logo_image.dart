import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';

class StockLogoImage extends StatelessWidget {
  final String ticker;
  final String logoUrl;
  final double size;

  const StockLogoImage({
    super.key,
    required this.ticker,
    required this.logoUrl,
    this.size = 40.0,
  });

  @override
  Widget build(BuildContext context) {
    final cleanUrl = logoUrl.trim();
    if (cleanUrl.isNotEmpty && (cleanUrl.startsWith('http://') || cleanUrl.startsWith('https://'))) {
      return CachedNetworkImage(
        imageUrl: cleanUrl,
        width: size,
        height: size,
        imageBuilder: (context, imageProvider) => Container(
          width: size,
          height: size,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            image: DecorationImage(image: imageProvider, fit: BoxFit.cover),
          ),
        ),
        placeholder: (context, url) => _buildFallbackAvatar(),
        errorWidget: (context, url, error) => _buildFallbackAvatar(),
      );
    }
    return _buildFallbackAvatar();
  }

  Widget _buildFallbackAvatar() {
    return Container(
      width: size,
      height: size,
      decoration: const BoxDecoration(
        color: Color(0xFF1E293B),
        shape: BoxShape.circle,
      ),
      alignment: Alignment.center,
      child: Text(
        ticker.length > 2 ? ticker.substring(0, 2) : ticker,
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: size * 0.38,
        ),
      ),
    );
  }
}
