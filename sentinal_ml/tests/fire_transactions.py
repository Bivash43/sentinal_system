import random
import time

import requests


API_URL = "http://localhost:8080/api/transactions/analyze"
TOTAL_FEATURES = 30


def generate_features():
    return [round(random.uniform(-5.0, 5.0), 6) for _ in range(TOTAL_FEATURES)]


def send_transaction(index, scenario, amount, card_number):
    payload = {
        "amount": amount,
        "cardNumber": card_number,
        "currency": "USD",
        "merchantId": f"M_{1000 + index}",
        "features": generate_features(),
    }

    try:
        response = requests.post(API_URL, json=payload, timeout=10)
        print(
            f"[{index:02d}] {scenario:<14} card={card_number} "
            f"amount={amount:>8.2f} -> status={response.status_code}"
        )
    except requests.RequestException as exc:
        print(f"[{index:02d}] {scenario:<14} card={card_number} -> request failed: {exc}")


def main():
    print("Sending 20 transactions...")

    tx_index = 1

    # 1) 5 normal transactions
    for _ in range(5):
        send_transaction(
            tx_index,
            "normal",
            amount=round(random.uniform(20.0, 250.0), 2),
            card_number=f"41111111111111{tx_index:02d}",
        )
        tx_index += 1
        time.sleep(0.2)

    # 2) 10 high-frequency transactions (same card, minimal delay)
    high_freq_card = "5555555555554444"
    for _ in range(10):
        send_transaction(
            tx_index,
            "high-frequency",
            amount=round(random.uniform(10.0, 90.0), 2),
            card_number=high_freq_card,
        )
        tx_index += 1
        time.sleep(0.05)

    # 3) 5 high-amount transactions
    for i in range(5):
        send_transaction(
            tx_index,
            "high-amount",
            amount=round(random.uniform(8000.0, 25000.0), 2),
            card_number=f"40000000000099{i}",
        )
        tx_index += 1
        time.sleep(0.2)

    print("Done.")


if __name__ == "__main__":
    main()
