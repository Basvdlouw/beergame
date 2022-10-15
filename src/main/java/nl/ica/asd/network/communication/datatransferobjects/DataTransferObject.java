package nl.ica.asd.network.communication.datatransferobjects;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntry;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.AppendEntryResult;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionRequest;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.InitialConnectionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.SerializationExceptionResponse;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.Vote;
import nl.ica.asd.network.communication.datatransferobjects.directlyserializable.VoteRequest;


@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@dataTransferObjectType")
@JsonSubTypes({
    @Type(value = AppendEntry.class, name = "AppendEntry"),
    @Type(value = AppendEntryResult.class, name = "AppendEntryResult"),
    @Type(value = InitialConnectionRequest.class, name = "InitialConnectionRequest"),
    @Type(value = InitialConnectionResponse.class, name = "InitialConnectionResponse"),
    @Type(value = SerializationExceptionResponse.class, name = "SerializationExceptionResponse"),
    @Type(value = Vote.class, name = "Vote"),
    @Type(value = VoteRequest.class, name = "VoteRequest")
})
public abstract class DataTransferObject {

}
