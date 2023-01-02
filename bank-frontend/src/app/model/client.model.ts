import { BankAccount } from "./bank-account.model";

export class Client {
    id: number;
    merchantId: string; 
    merchantPassword: string;
    name: string;
    email: string;
    bankAccount: BankAccount;
}
