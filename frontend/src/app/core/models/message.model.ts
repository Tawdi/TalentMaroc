export interface ApplicationMessage {
  id: number;
  applicationId: number;
  senderUserId: string;
  content: string;
  sentAt: string;
  read: boolean;
}

