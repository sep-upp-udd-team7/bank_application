import { CreditCard } from "./credit-card.model";

export class BankAccount {
    id: number;
    creditCard: CreditCard;
    bankAccountNumber: string;
    availableFunds: number;
    reservedFunds: number;
}
