# Ausolan Telegram Bot

[![Docker Pulls](https://img.shields.io/docker/pulls/ferpinan/ausolan-telegram-bot.svg)](https://hub.docker.com/r/ferpinan/ausolan-telegram-bot)
[![Docker Image Size](https://img.shields.io/docker/image-size/ferpinan/ausolan-telegram-bot/latest.svg)](https://hub.docker.com/r/ferpinan/ausolan-telegram-bot)

A Telegram bot that provides menu information from Ausolan catering services.

## Disclaimer

**This project is an unofficial community tool and is not affiliated with Ausolan.**

## Quick Start

### Using Docker

```bash
docker run -d \
  --name ausolan-telegram-bot \
  --restart always \
  -e TELEGRAM_BOT_TOKEN=your_bot_token_here \
  -e TELEGRAM_CHAT_TOKEN=@your_channel_here \
  -e AUSOLAN_CENTROPK=your_centro_pk \
  -e AUSOLAN_CENTROENVIOPK=your_centro_envio_pk \
  -e AUSOLAN_DIETAPK=your_dieta_pk \
  -e AUSOLAN_IDIOMAPK=your_idioma_pk \
  -e AUSOLAN_MENUPK=your_menu_pk \
  -e AUSOLAN_SERVICIOPK=your_servicio_pk \
  ferpinan/ausolan-telegram-bot:latest
```

### Using Docker Compose

Create a `docker-compose.yml` file:

```yaml
services:
  ausolan-telegram-bot:
    image: ferpinan/ausolan-telegram-bot:latest
    container_name: ausolan-telegram-bot
    environment:
      # Telegram Configuration
      - TELEGRAM_BOT_TOKEN=your_bot_token_here
      - TELEGRAM_CHAT_TOKEN=@your_channel_here
      # Ausolan Configuration
      - AUSOLAN_BASEURL=https://apimenuo.ausolan.com/plapi
      - AUSOLAN_CENTROPK=your_centro_pk
      - AUSOLAN_CENTROENVIOPK=your_centro_envio_pk
      - AUSOLAN_DIETAPK=your_dieta_pk
      - AUSOLAN_IDIOMAPK=your_idioma_pk
      - AUSOLAN_MENUPK=your_menu_pk
      - AUSOLAN_SERVICIOPK=your_servicio_pk
    restart: always
```

Example for Amassorrain Ikastola:

```yaml
services:
  ausolan-telegram-bot:
    image: ferpinan/ausolan-telegram-bot:latest
    container_name: ausolan-telegram-bot
    environment:
      # Telegram Config Variables
      - TELEGRAM_BOT_TOKEN=7637911987:AAHLPYMyFSFbAyin_z6FmGg-IOJYUoNTRDI
      - TELEGRAM_CHAT_TOKEN=@amassorrainmenudev
      # Ausolan 
      - AUSOLAN_BASEURL=https://apimenuo.ausolan.com/plapi
      - AUSOLAN_CENTROPK=4c892ec4983a49dfb156b2830157a241
      - AUSOLAN_CENTROENVIOPK=385f5df365de42bfb890b291008e12c8
      - AUSOLAN_DIETAPK=d12fe25a9bbd49239e3cd65eef62e7c5
      - AUSOLAN_IDIOMAPK=00242C08E94B10101810432101292479
      - AUSOLAN_MENUPK=cebf5d72a8ba4ac2b20bb2ac008f5403
      - AUSOLAN_SERVICIOPK=6f6096586ff248189d7d535b8509f411
    restart: always
```

Then run:

```bash
docker-compose up -d
```

## Environment Variables

| Variable | Required | Default Value | Description |
|----------|----------|---------------|-------------|
| `TELEGRAM_BOT_TOKEN` | **Yes** | - | Your Telegram Bot API token from [@BotFather](https://t.me/botfather) |
| `TELEGRAM_CHAT_TOKEN` | **Yes** | - | Target Telegram channel or chat ID (e.g., `@channelname` or `-1001234567890`) |
| `AUSOLAN_BASEURL` | No | `https://apimenuo.ausolan.com/plapi` | Base URL for Ausolan API |
| `AUSOLAN_CENTROPK` | **Yes** | - | Center primary key identifier |
| `AUSOLAN_CENTROENVIOPK` | **Yes** | - | Center shipment primary key identifier |
| `AUSOLAN_DIETAPK` | **Yes** | - | Diet primary key identifier |
| `AUSOLAN_IDIOMAPK` | **Yes** | - | Language primary key identifier |
| `AUSOLAN_MENUPK` | **Yes** | - | Menu primary key identifier |
| `AUSOLAN_SERVICIOPK` | **Yes** | - | Service primary key identifier |

## Notes

- All Ausolan PK (Primary Key) values are specific to your catering service configuration
- The bot requires a valid Telegram Bot Token obtained from [@BotFather](https://t.me/botfather)
- Make sure the bot has permission to post in the specified channel/chat

## Configuration

To obtain your specific Ausolan configuration values (PK identifiers), you'll need to inspect the API calls from the official Ausolan menu platform or contact your catering service provider.

## License

This is an open-source community project. Please ensure you comply with Ausolan's terms of service when using their API.

## Contributing

Contributions, issues, and feature requests are welcome!

## Support

For issues and questions, please open an issue on the project repository.

---

**Remember:** This is an unofficial tool created by the community and is not endorsed or supported by Ausolan.