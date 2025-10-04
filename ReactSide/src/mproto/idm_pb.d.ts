import * as jspb from 'google-protobuf'

import * as google_protobuf_empty_pb from 'google-protobuf/google/protobuf/empty_pb'; // proto import: "google/protobuf/empty.proto"


export class AuthRequest extends jspb.Message {
  getUsername(): string;
  setUsername(value: string): AuthRequest;

  getPassword(): string;
  setPassword(value: string): AuthRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AuthRequest.AsObject;
  static toObject(includeInstance: boolean, msg: AuthRequest): AuthRequest.AsObject;
  static serializeBinaryToWriter(message: AuthRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AuthRequest;
  static deserializeBinaryFromReader(message: AuthRequest, reader: jspb.BinaryReader): AuthRequest;
}

export namespace AuthRequest {
  export type AsObject = {
    username: string,
    password: string,
  }
}

export class AuthResponse extends jspb.Message {
  getTokenvalue(): string;
  setTokenvalue(value: string): AuthResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): AuthResponse.AsObject;
  static toObject(includeInstance: boolean, msg: AuthResponse): AuthResponse.AsObject;
  static serializeBinaryToWriter(message: AuthResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): AuthResponse;
  static deserializeBinaryFromReader(message: AuthResponse, reader: jspb.BinaryReader): AuthResponse;
}

export namespace AuthResponse {
  export type AsObject = {
    tokenvalue: string,
  }
}

export class TokenRequest extends jspb.Message {
  getTokenvalue(): string;
  setTokenvalue(value: string): TokenRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TokenRequest.AsObject;
  static toObject(includeInstance: boolean, msg: TokenRequest): TokenRequest.AsObject;
  static serializeBinaryToWriter(message: TokenRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TokenRequest;
  static deserializeBinaryFromReader(message: TokenRequest, reader: jspb.BinaryReader): TokenRequest;
}

export namespace TokenRequest {
  export type AsObject = {
    tokenvalue: string,
  }
}

export class TokenValidationResponse extends jspb.Message {
  getValid(): boolean;
  setValid(value: boolean): TokenValidationResponse;

  getExpired(): boolean;
  setExpired(value: boolean): TokenValidationResponse;

  getSubject(): string;
  setSubject(value: string): TokenValidationResponse;

  getRole(): string;
  setRole(value: string): TokenValidationResponse;

  getEmail(): string;
  setEmail(value: string): TokenValidationResponse;

  getMessage(): string;
  setMessage(value: string): TokenValidationResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): TokenValidationResponse.AsObject;
  static toObject(includeInstance: boolean, msg: TokenValidationResponse): TokenValidationResponse.AsObject;
  static serializeBinaryToWriter(message: TokenValidationResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): TokenValidationResponse;
  static deserializeBinaryFromReader(message: TokenValidationResponse, reader: jspb.BinaryReader): TokenValidationResponse;
}

export namespace TokenValidationResponse {
  export type AsObject = {
    valid: boolean,
    expired: boolean,
    subject: string,
    role: string,
    email: string,
    message: string,
  }
}

export class DestroyTokenResponse extends jspb.Message {
  getSuccess(): boolean;
  setSuccess(value: boolean): DestroyTokenResponse;

  getMessage(): string;
  setMessage(value: string): DestroyTokenResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): DestroyTokenResponse.AsObject;
  static toObject(includeInstance: boolean, msg: DestroyTokenResponse): DestroyTokenResponse.AsObject;
  static serializeBinaryToWriter(message: DestroyTokenResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): DestroyTokenResponse;
  static deserializeBinaryFromReader(message: DestroyTokenResponse, reader: jspb.BinaryReader): DestroyTokenResponse;
}

export namespace DestroyTokenResponse {
  export type AsObject = {
    success: boolean,
    message: string,
  }
}

export class UserRequest extends jspb.Message {
  getEmail(): string;
  setEmail(value: string): UserRequest;

  getPassword(): string;
  setPassword(value: string): UserRequest;

  getRole(): string;
  setRole(value: string): UserRequest;

  getTokenvalue(): string;
  setTokenvalue(value: string): UserRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UserRequest.AsObject;
  static toObject(includeInstance: boolean, msg: UserRequest): UserRequest.AsObject;
  static serializeBinaryToWriter(message: UserRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UserRequest;
  static deserializeBinaryFromReader(message: UserRequest, reader: jspb.BinaryReader): UserRequest;
}

export namespace UserRequest {
  export type AsObject = {
    email: string,
    password: string,
    role: string,
    tokenvalue: string,
  }
}

export class UserResponse extends jspb.Message {
  getId(): number;
  setId(value: number): UserResponse;

  getEmail(): string;
  setEmail(value: string): UserResponse;

  getRole(): string;
  setRole(value: string): UserResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UserResponse.AsObject;
  static toObject(includeInstance: boolean, msg: UserResponse): UserResponse.AsObject;
  static serializeBinaryToWriter(message: UserResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UserResponse;
  static deserializeBinaryFromReader(message: UserResponse, reader: jspb.BinaryReader): UserResponse;
}

export namespace UserResponse {
  export type AsObject = {
    id: number,
    email: string,
    role: string,
  }
}

export class UserIdRequest extends jspb.Message {
  getId(): number;
  setId(value: number): UserIdRequest;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UserIdRequest.AsObject;
  static toObject(includeInstance: boolean, msg: UserIdRequest): UserIdRequest.AsObject;
  static serializeBinaryToWriter(message: UserIdRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UserIdRequest;
  static deserializeBinaryFromReader(message: UserIdRequest, reader: jspb.BinaryReader): UserIdRequest;
}

export namespace UserIdRequest {
  export type AsObject = {
    id: number,
  }
}

export class UserList extends jspb.Message {
  getUsersList(): Array<UserResponse>;
  setUsersList(value: Array<UserResponse>): UserList;
  clearUsersList(): UserList;
  addUsers(value?: UserResponse, index?: number): UserResponse;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): UserList.AsObject;
  static toObject(includeInstance: boolean, msg: UserList): UserList.AsObject;
  static serializeBinaryToWriter(message: UserList, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): UserList;
  static deserializeBinaryFromReader(message: UserList, reader: jspb.BinaryReader): UserList;
}

export namespace UserList {
  export type AsObject = {
    usersList: Array<UserResponse.AsObject>,
  }
}

